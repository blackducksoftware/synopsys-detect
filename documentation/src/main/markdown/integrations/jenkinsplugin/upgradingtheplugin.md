# Upgrading from previous plugin versions
If you have already configured [solution_name] for Jenkins across a number of jobs, Synopsys provides a script to make the process easier. The script automatically migrates the [solution_name] for Jenkins post-build step in your FreeStyle jobs from the version 1.5.0 format to the new 2.0.x format. 
Run the script by copying and pasting it into the Jenkins Script Console, then clicking **Run**.  For more information, refer to <https://wiki.jenkins.io/display/JENKINS/Jenkins+Script+Console>.

This script is run once by a Jenkins administrator after upgrading the plugin from 1.5.0 to 2.0.x. The script performs two transformations: one to the [solution_name] system configuration, and the other to the post-build steps of FreeStyle jobs that were configured with version 1.5.0 of the plugin. The groovy script only makes changes to the FreeStyle jobs if the data migration is successful for that job because saving a Jenkins job erases unreadable data stored in that job. Therefore, Synopsys recommends migrating any other unreadable data from these jobs before running this script.

The migration script is available on [GitHub](https://github.com/jenkinsci/synopsys-detect-plugin/tree/master/groovy-scripts).

**Note:** Be careful if you use this script as it is an example that makes changes to jobs.
