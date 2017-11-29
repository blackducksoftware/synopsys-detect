[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$Version = "1.0.5"

function Detect {
    Write-Host "Detect Powershell Script $Version"
    Get-Detect -DetectJarFile [ref] $DetectJarFile
    $DetectArgs = $args;
    $DetectExitCode = Invoke-Detect -DetectJarFile $DetectJarFile -DetectArgs $DetectArgs
    exit $DetectExitCode
}

function Invoke-Detect ($DetectJarFile, $DetectArgs) {
    Write-Host "Jar file: $DetectJarFile"
    $JavaArgs = @("-jar", $DetectJarFile)
    $AllArgs =  $JavaArgs + $DetectArgs
    Write-Host "Running detect : $AllArgs"
    $DetectProcess = Start-Process java -ArgumentList $AllArgs -NoNewWindow -Wait -PassThru
    $DetectExitCode = $DetectProcess.ExitCode;
    Write-Host "Result code of $DetectExitCode, exiting"
    return $DetectExitCode    
}

function Get-Detect ([ref] $DetectJarFile) {
    $DetectVersion = Get-EnvironmentVariable -Name "DetectVersion" -DefaultValue "";
    $DetectJarFolder = Get-EnvironmentVariable -Name "DetectJarFolder" -DefaultValue "$HOME\tmp";
    #TODO: Mirror the functionality of the shell script and allow Java opts and GET (InvokeWebRequest?) opts.
    #$DETECT_JAVA_OPTS = Get-EnvironmentVariable -Name "DETECT_JAVA_OPTS" -DefaultValue "";
    #$DETECT_GET_OPTS = Get-EnvironmentVariable -Name "DETECT_GET_OPTS" -DefaultValue "";

    Write-Host "Checking for detect jar folder: $DetectJarFolder"

    If(!(Test-Path $DetectJarFolder)) {
        Write-Host "Creating detect jar folder: $DetectJarFolder"
        New-Item -ItemType Directory -Force -Path $DetectJarFolder
    }

    if ($DetectVersion -eq "") {
        Write-Host "Finding latest detect version."
        $DetectVersion = Invoke-WebRequest "https://test-repo.blackducksoftware.com/artifactory/api/search/latestVersion?g=com.blackducksoftware.integration&a=hub-detect&repos=bds-integrations-release"
        Write-Host "Resolved version $DetectVersion"
    }

    $DetectUrl="https://test-repo.blackducksoftware.com/artifactory/bds-integrations-release/com/blackducksoftware/integration/hub-detect/${DetectVersion}/hub-detect-${DetectVersion}.jar"
    $DetectJarFile="$DetectJarFolder\hub-detect-$DetectVersion.jar"

    Write-Host "Checking for existing detect jar."
    $DetectJarExists = Test-Path $DetectJarFile

    if (!$DetectJarExists){
        Write-Host "You don't have detect. Downloading now."
        Invoke-WebRequest $DetectUrl -OutFile $DetectJarFile
    }else{
        Write-Host "Existing detect jar file found."
    }

    Write-Host "Resolved detect jar: $DetectJarFile"
}


function Get-EnvironmentVariable($Name, $DefaultValue) {
    if (-not (Test-Path Env:$Name)) {
        return $DefaultValue;
    }else{
        return Get-ChildItem Env:$Name;
    }
}
