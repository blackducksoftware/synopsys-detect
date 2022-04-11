# Erlang/Hex/Rebar support

## Related properties

[Detector properties](https://community.synopsys.com/s/document-item?bundleId=integrations-detect&topicId=properties%2Fdetectors%2Fhex.html)

## Overview

The Rebar detector discovers dependencies of Erlang projects that use the Hex package manager.

The Rebar detector runs if [solution_name] finds a *rebar.config* file in your project.
A *rebar3* executable must be found on the PATH, or must be [provided](../properties/detectors/hex.md#rebar3-executable).

The Rebar detector runs the *rebar3 tree* command and parses the output for dependency information.



