# ${solution_name} code verification

Two methods are available to verify that the ${solution_name} code you run has not been tampered with since it was built by ${company_name}:
code signature verification and checksum verification.
Both methods apply to the ${solution_name} .jar file, and only offer protection when you run
${solution_name} by invoking the ${solution_name} .jar file directly (as opposed to invoking ${bash_script_name} or ${powershell_script_name}).

## Code signature verification

Code signature verification is the most secure method available for verifying ${solution_name} code. This method relies on Java tools.

It involves verifying the ${solution_name} .jar file that you download from [${binary_repo_url_base}](${binary_repo_url_base})
using a public key certificate that you download from ${script_repo_url_base} (which maps to github.com).
In the event that either one has been tampered with, verification will fail.

To verify the ${solution_name} .jar, you need to obtain the public key certificate:

    curl -O https://detect.synopsys.com/jar_verification.crt

Once you have the public key certificate (the jar_verification.crt file), import it into a keystore. You can create a new keystore (which is a file),
or import the certificate into an existing keystore. If the keystore into which you import the certificate does not exist, it will be created and you
will be prompted to supply a password for the new keystore. If the keystore does exist, you will be prompted for its password as you import the certificate
to it.

    keytool -import -alias "blackduck digicert" -file jar_verification.crt -keystore {your keystore file}

The Common Name (CN) field in the certificate will be "Black Duck Software, Inc.". We recommend you check this to be sure you are
not importing a different certificate into your keystore, but the integrity of the code verification process does not depend on you verifying
the contents of the certificate (the next step will do that).

Finally, verify that the code signature in your ${solution_name} .jar file matches this certificate:

    jarsigner -verify {your ${solution_name} .jar file} -keystore {your keystore file}

The output should be `jar verified.` (with no warnings).

## Checksum verification

Checksum verification provides less protection against tampering than code signature verification provices because
in the unlikely scenario the [binary repository](binary_repo_url_base) has been compromised, an attacker could alter
both the .jar and the checksum. But checksum verification does provide some degree of protection
against other attack scenarios.

The binary repository provides SHA-256, SHA-1, and MD5 checksums for each ${solution_name} .jar
file. To find it, navigate to the .jar file in
[the binary repository](${binary_repo_ui_url_base}/bds-integrations-release/com/synopsys/integration/synopsys-detect),
and scroll to the bottom of the page. Various tools (such as md5sum, sha1sum, and sha256sum on Linux, and certutil and Get-FileHash on Windows) are available for
calculating checksums of files on your computer. Use one of those tools to get a checksum for your copy of the ${solution_name} .jar, and compare it
to the corresponding checksum on the binary repository page to make sure they match.

