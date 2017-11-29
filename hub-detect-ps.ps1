[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12


function Detect {
    $SCRIPT_ARGS = $args;
    $DETECT = Get-Detect;
    Invoke-Detect -DetectJar $DETECT -ScriptArgs $SCRIPT_ARGS
}

function Invoke-Detect ($DetectJar, $ScriptArgs) {
    $JAVA_ARGUMENTS = @("-jar", $DetectJar)
    $DETECT_ARGUMENTS =  $JAVA_ARGUMENTS + $ScriptArgs
    Write-Host "Executing detect with parameters $DetectJar"
    Write-Host "Executing detect with parameters $JAVA_ARGUMENTS"
    Write-Host "Executing detect with parameters $DETECT_ARGUMENTS"
    $DetectProcess = Start-Process java -ArgumentList $DETECT_ARGUMENTS -NoNewWindow -Wait
    $ExitCode = $DetectProcess.ExitCode;
    Write-Host "Result code of $ExitCode, exiting"
    exit $ExitCode
}

function Get-Detect () {
    Write-Host "Downloading Detect"

    $DETECT_RELEASE_VERSION = Get-EnvironmentVariable -Name "DETECT_RELEASE_VERSION" -DefaultValue "";
    $DETECT_JAR_PATH = Get-EnvironmentVariable -Name "DETECT_JAR_PATH" -DefaultValue "$HOME\tmp";
    $DETECT_JAVA_OPTS = Get-EnvironmentVariable -Name "DETECT_JAVA_OPTS" -DefaultValue "";
    #$DETECT_GET_OPTS = Get-EnvironmentVariable -Name "DETECT_GET_OPTS" -DefaultValue "";

    Write-Host "Resolved Variables"
    Write-Host "Detect Jar Path: $DETECT_JAR_PATH"

    If(!(Test-Path $DETECT_JAR_PATH)) {
        Write-Host "Creating detect jar directory $DETECT_JAR_PATH"
        New-Item -ItemType Directory -Force -Path $DETECT_JAR_PATH
    }

    if ($DETECT_RELEASE_VERSION -eq "") {
        Write-Host "Getting latest version."
        $DETECT_RELEASE_VERSION = Invoke-WebRequest "https://test-repo.blackducksoftware.com/artifactory/api/search/latestVersion?g=com.blackducksoftware.integration&a=hub-detect&repos=bds-integrations-release"
        Write-Host "Resolved version $DETECT_RELEASE_VERSION"
    }

    $DETECT_SOURCE="https://test-repo.blackducksoftware.com/artifactory/bds-integrations-release/com/blackducksoftware/integration/hub-detect/${DETECT_RELEASE_VERSION}/hub-detect-${DETECT_RELEASE_VERSION}.jar"
    $DETECT_DESTINATION="$DETECT_JAR_PATH\hub-detect-$DETECT_RELEASE_VERSION.jar"

    $DETECT_EXISTS = Test-Path $DETECT_DESTINATION

    if (!$DETECT_EXISTS){
        Write-Host "You don't have detect. Downloading now."
        Write-Host "Downloading to $DETECT_DESTINATION"
        Invoke-WebRequest $DETECT_SOURCE -OutFile $DETECT_DESTINATION
    }

    return $DETECT_DESTINATION
}


function Get-EnvironmentVariable($Name, $DefaultValue) {
    if (-not (Test-Path Env:$Name)) {
        Write-Host "Default"
        return $DefaultValue;
    }else{
        Write-Host "Value"
        return Get-ChildItem Env:$Name;
    }
}
