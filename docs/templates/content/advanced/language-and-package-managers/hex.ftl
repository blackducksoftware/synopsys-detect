# Erlang/Hex/Rebar support

The Rebar detector can discover dependencies of Erlang projects that use the Hex package manager.

The Rebar detector will run if ${solution_name} finds a *rebar.config* file in your project.
A *rebar3* executable must be found on the PATH, or must be [provided](/properties/Detectors/hex/#rebar3-executable).

The Rebar detector runs the *rebar3 tree* command and parses the output for dependency information.



