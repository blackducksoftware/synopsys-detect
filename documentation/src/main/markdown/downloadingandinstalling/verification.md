# [solution_name] Code Verification

Two methods are available to verify that the [solution_name] code you run has not been tampered with since it was built by [company_name]:
code signature verification and checksum verification.
Both methods apply to the [solution_name] .jar file, and only offer protection when you run
[solution_name] by invoking the [solution_name] .jar file directly (as opposed to invoking [bash_script_name] or [powershell_script_name]).

## Code signature verification

Code signature verification is the most secure method available for verifying [solution_name] code. This method relies on Java tools.

It involves verifying the [solution_name] .jar file that you download from the location specified in [download locations](downloadlocations.md),
using the Java *jarsigner* tool. In the event that the .jar has been tampered with, verification will fail.

To verify the [solution_name] .jar:

jarsigner -verify -strict {your [solution_name] .jar file}

The output should be `jar verified.` (with no warnings).

## Checksum verification

Checksum verification provides less protection against tampering than code signature verification provices because
in the unlikely scenario the
[division_name] [binary_repo_type] server
has been compromised, an attacker could alter
both the .jar and the checksum. But checksum verification does provide some degree of protection
against other attack scenarios.

The binary repository provides SHA-256, SHA-1, and MD5 checksums for each [solution_name] .jar
file. To find it, navigate to the .jar file in the
[division_name] [binary_repo_type] server specified in [download locations](downloadlocations.md),
and scroll to the bottom of the page. Various tools (such as md5sum, sha1sum, and sha256sum on Linux, and certutil and Get-FileHash on Windows) are available for
calculating checksums of files on your computer. Use one of those tools to get a checksum for your copy of the [solution_name] .jar, and compare it
to the corresponding checksum on the binary repository page to make sure they match.
