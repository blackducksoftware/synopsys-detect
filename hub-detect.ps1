#Detect Powershell Script
#Recommended Invocation: powershell "iwr https://blackducksoftware.github.io/hub-detect/hub-detect.ps1?$(Get-Random) | iex; detect"

function Get-EnvironmentVariable($Key, $DefaultValue) { if (-not (Test-Path Env:$Key)) { return $DefaultValue; }else{ return (Get-ChildItem Env:$Key).Value; } }

# DETECT_LATEST_RELEASE_VERSION should be set in your
# environment if you wish to use a version different
# from LATEST.
$EnvDetectDesiredVersion = Get-EnvironmentVariable -Key "DETECT_LATEST_RELEASE_VERSION" -DefaultValue "";

# If you would like to enable the shell script to use
# the latest snapshot instead of the latest release,
# specify DETECT_USE_SNAPSHOT=1 in your environment.
# The default is to NOT use snapshots. If you enable
# snapshots, the jar file will be downloaded whenever
# a new commit is added to the master branch.
$EnvDetectUseSnapshot = Get-EnvironmentVariable -Key "DETECT_USE_SNAPSHOT" -DefaultValue "0";

# To override the default location specify your own DETECT_JAR_PATH
# Otherwise, if the environment temp folder is set, it will be used.
# Otherwise, a temporary folder will be created in your home directory
$EnvDetectFolder = Get-EnvironmentVariable -Key "DETECT_JAR_PATH" -DefaultValue "";
$EnvTempFolder = Get-EnvironmentVariable -Key "TMP" -DefaultValue "";
$EnvHomeTempFolder = "$HOME\tmp"

#TODO: Mirror the functionality of the shell script and allow Java opts and GET (InvokeWebRequest?) opts.

# If you want to pass any java options to the
# invocation, specify DETECT_JAVA_OPTS in your
# environment. For example, to specify a 6 gigabyte
# heap size, you would set DETECT_JAVA_OPTS=-Xmx6G.
#$DetectJavaOpts = Get-EnvironmentVariable -Key "DETECT_JAVA_OPTS" -DefaultValue "";

# If you want to pass any additional options to
# curl, specify DETECT_CURL_OPTS in your environment.
# For example, to specify a proxy, you would set
# DETECT_CURL_OPTS=--proxy http://myproxy:3128
#$DetectGetOpts = Get-EnvironmentVariable -Key "DETECT_CURL_OPTS" -DefaultValue "";

$Version = "0.1.9"

$DetectReleaseBaseUrl = "https://test-repo.blackducksoftware.com/artifactory/bds-integrations-release/com/blackducksoftware/integration/hub-detect"
$DetectSnapshotBaseUrl = "https://test-repo.blackducksoftware.com/artifactory/bds-integrations-snapshot/com/blackducksoftware/integration/hub-detect"
$DetectCommitUrl = "https://blackducksoftware.github.io/hub-detect/latest-commit-id.txt"
$DetectVersionUrl = "https://test-repo.blackducksoftware.com/artifactory/api/search/latestVersion?g=com.blackducksoftware.integration&a=hub-detect&repos=bds-integrations-release"

[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12 #Enable TLS2

function Detect {
    Write-Host "Detect Powershell Script $Version"

    Write-Host "Initializing detect folder."
    $DetectFolder = Initialize-DetectFolder -DetectFolder $EnvDetectFolder -TempFolder $EnvTempFolder -HomeTempFolder $EnvHomeTempFolder

    Write-Host "Getting detect."
    if ($EnvDetectUseSnapshot -eq "1"){
        $DetectJarFile = Get-DetectSnapshotJar -DetectFolder $DetectFolder -DetectVersion $EnvDetectDesiredVersion
    }else{
        $DetectJarFile = Get-DetectJar -DetectFolder $DetectFolder -DetectVersion $EnvDetectDesiredVersion
    }

    Write-Host "Executing detect."
    $DetectArgs = $args;
    $DetectExitCode = Invoke-Detect -DetectJar $DetectJarFile -DetectArgs $DetectArgs
    exit $DetectExitCode
}

function Get-DetectSnapshotJar ($DetectFolder, $DetectVersion) {
    if ($DetectVersion -eq ""){
        $DetectVersion = "latest-SNAPSHOT"
    }
    
    Write-Host "Using detect version $DetectVersion"

    $DetectJarFile = "$DetectFolder\hub-detect-$DetectVersion.jar"
    $DetectCurrentCommitFile = "$DetectFolder\hub-detect-latest-commit.txt"

    $DetectJarExists = Test-Path $DetectJarFile
    $DetectCurrentCommitFileExists = Test-Path $DetectCurrentCommitFile
    $DetectLatestCommit = Invoke-WebRequest $DetectCommitUrl
    $DetectLatestCommit = $DetectLatestCommit.ToString().Trim()
    Write-Host "Detect jar exists '$DetectJarExists', commit file exists '$DetectCurrentCommitFileExists', latest commit '$DetectLatestCommit'"

    $Download = $TRUE;
    if ($DetectJarExists -and $DetectCurrentCommitFileExists){
        $DetectCurrentCommit = Get-Content $DetectCurrentCommitFile
        $DetectCurrentCommit = $DetectCurrentCommit.Trim()
        Write-Host "Current commit '$DetectCurrentCommit'"
        if ($DetectCurrentCommit -eq $DetectLatestCommit){
            $Download = $FALSE;
        }
    }

    if ($Download){
        $DetectUrl = Join-DetectUrl -DetectBaseUrl $DetectSnapshotBaseUrl -DetectVersion $DetectVersion
        Receive-DetectJar -DetectUrl $DetectUrl -DetectJarFile $DetectJarFile
        Set-Content $DetectCurrentCommitFile $DetectLatestCommit
    }else{
        Write-Host "You have already downloaded the latest file, so the local file will be used."
    }
    
    return $DetectJarFile    
}

function Get-DetectJar ($DetectFolder, $DetectVersion) {
    if ($DetectVersion -eq ""){
        $DetectVersion = Receive-DetectLatestVersion
    }

	Write-Host "Using detect version $DetectVersion"

    $DetectJarFile = "$DetectFolder\hub-detect-$DetectVersion.jar"

    $DetectJarExists = Test-Path $DetectJarFile
    Write-Host "Detect jar exists '$DetectJarExists'"

    if (!$DetectJarExists){
        $DetectUrl = Join-DetectUrl -DetectBaseUrl $DetectReleaseBaseUrl -DetectVersion $DetectVersion
        Receive-DetectJar -DetectUrl $DetectUrl -DetectJarFile $DetectJarFile
    }else{
        Write-Host "You have already downloaded the latest file, so the local file will be used."
    }

    return $DetectJarFile    
}

function Invoke-Detect ($DetectJarFile, $DetectArgs) {
    $JavaArgs = @("-jar", $DetectJarFile)
    $AllArgs =  $JavaArgs + $DetectArgs
    Write-Host "Running detect: $AllArgs"
    $DetectProcess = Start-Process java -ArgumentList $AllArgs -NoNewWindow -Wait -PassThru
    $DetectExitCode = $DetectProcess.ExitCode;
    Write-Host "Result code of $DetectExitCode, exiting"
    return $DetectExitCode
}

function Join-DetectUrl ($DetectBaseUrl, $DetectVersion) {
    return "$DetectBaseUrl/${DetectVersion}/hub-detect-${DetectVersion}.jar"
}

function Initialize-DetectFolder ($DetectFolder, $TempFolder, $HomeTempFolder) {
    if ($DetectFolder -ne ""){
        Write-Host "Using supplied detect folder: $DetectFolder"
        return Initialize-Folder -Folder $DetectFolder
    }

    if ($TempFolder -ne ""){
        Write-Host "Using system temp folder: $TempFolder"
        return Initialize-Folder -Folder $TempFolder
    }

    return Initialize-Folder -Folder $HomeTempFolder
}

function Initialize-Folder ($Folder) {
    If(!(Test-Path $Folder)) {
        Write-Host "Created folder: $Folder"
        New-Item -ItemType Directory -Force -Path $Folder | Out-Null #Pipe to Out-Null to prevent dirtying to the function output
    }
    return $Folder
}

function Receive-DetectLatestVersion {
    Write-Host "Finding latest detect version."
    $DetectVersion = Invoke-WebRequest $DetectVersionUrl
    Write-Host "Resolved version $DetectVersion"
    return $DetectVersion
}

function Receive-DetectJar ($DetectUrl, $DetectJarFile) {
    Write-Host "You don't have detect. Downloading now."
    Write-Host "Using url $DetectUrl"
    $Request = Invoke-WebRequest $DetectUrl -OutFile $DetectJarFile
    $DetectJarExists = Test-Path $DetectJarFile
    Write-Host "Downloaded detect jar successfully '$DetectJarExists'"
}
