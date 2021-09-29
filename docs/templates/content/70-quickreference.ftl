# Quick reference

## Quick Links
* [All Properties](../properties/all-properties/)
* [Exit Codes](../advanced/troubleshooting/exit-codes/)

## ${solution_name} Modes

${solution_name} provides the following modes to assist with learning, troubleshooting, and setup.

| Mode | Command line option | Alt. option | Description |
| ---- | ------------------- | ----------- | ----------- |
| Help | --help | -h | Provides basic help information (including how to get more detailed help). |
| Interactive | --interactive | -i | Guides you through configuring ${solution_name}. |
| Diagnostic | --detect.diagnostic=true | N/A | Creates a zip file of diagnostic information for support. |
| Air Gap Creation | --zip | -z | Creates an air gap zip that you can use with the detect.*.air.gap.path arguments for running ${solution_name} offline. Optionally you can follow --zip with a space and an argument (for example: --zip FULL) to customize the air gap zip. Possible values: FULL (produce a full air gap zip; the default), NO_DOCKER (do not include the Docker Inspector). |
