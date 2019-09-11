# Common Tasks

This page provides some quick examples of common detect tasks. 

## Running only a specific tool
```
--detect.tools=SIGNATURE_SCAN
```

## Increasing the log level
```
--logging.level.detect=DEBUG
```

## Connecting to Black Duck
```
--blackduck.url=http://example.com
--blackduck.api.token=token
```
If you are running a self-signed Black Duck, you will want to trust your certificate (not recommended for production).
```
--blackduck.trust.cert=true
```
