[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$Version = "1.0.3"

function Detect {
    Write-Host "Detect Powershell Script $Version"
    $DetectArgs = $args;
    $DetectJar = Get-Detect;
    Write-Host "Invoking jar: $DetectJar"
    $DetectExitCode = Invoke-Detect -DetectJar $DetectJar -DetectArgs $DetectArgs
    exit $DetectExitCode
}

function Invoke-Detect ($DetectJar, $DetectArgs) {
    $JavaArgs = @("-jar", $DetectJar)
    if ($DetectArgs.Count > 0) { #If we try to add DetectArgs and it has no values it makes $AllArgs an Object[] instead of String[] 
        $AllArgs =  $JavaArgs + $DetectArgs
    }else{
        $AllArgs = $JavaArgs
    }
    Write-Host "Running detect: $DetectJar"
    Write-Host "Running detect: $JavaArgs"
    Write-Host "Running detect: $AllArgs"
    $DetectProcess = Start-Process java -ArgumentList $AllArgs -NoNewWindow -Wait
    $DetectExitCode = $DetectProcess.ExitCode;
    Write-Host "Result code of $DetectExitCode, exiting"
    return $DetectExitCode    
}

function Get-Detect () {
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

    Write-Host "Checking for existing detect jar file: $DetectJarFile"
    $DetectJarExists = Test-Path $DetectJarFile

    if (!$DetectJarExists){
        Write-Host "You don't have detect. Downloading now."
        Invoke-WebRequest $DetectUrl -OutFile $DetectJarFile
    }else{
        Write-Host "Using existing jar file."
    }

    Write-Host "Resolved detect jar: $DetectJarFile"
    return $DetectJarFile
}


function Get-EnvironmentVariable($Name, $DefaultValue) {
    if (-not (Test-Path Env:$Name)) {
        return $DefaultValue;
    }else{
        return Get-ChildItem Env:$Name;
    }
}
