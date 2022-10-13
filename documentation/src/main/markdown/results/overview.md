# Viewing and Managing Scan Results

## Online mode

To view and manage your [solution_name] scan results after running [solution_name] online, do the following.

- In the [solution_name] output look for "Detect Result" and copy the Black Duck Project BOM URL as shown in the following example:

````
2020-06-11 06:35:39 INFO [main] ---======== Detect Result ========
2020-06-11 06:35:39 INFO [main] --- Black Duck Project BOM: https://my-hub-docker/api/projects/d8f798f1-1901-4902-aec7-f2e1cf2e4958/versions/6a8938e9-3615-40dd-8386-3bcb4ba52bec/components
````
- Open the [blackduck_product_name] Project BOM URL in a browser to view the scan results in [blackduck_product_name].
- To find your scan in [blackduck_product_name], go to your [blackduck_product_name] instance and click Scans to see a list of scans on the Scans page.

For help with viewing and analyzing your scan results go to the [blackduck_product_name] Help page navigation menu at https://\<Your hub host\>/doc/Welcome.htm

## Offline mode

To view and manage your [solution_name] scan results after running [solution_name] offline (with property *blackduck.offline.mode* set to *true*), do the following.

- In the [solution_name] output (near the beginning), look for the value of "Run directory". The output files will be written into subdirectories of the run directory. For example:

````
2022-03-07 15:46:29 EST INFO  [main] --- Run directory: /Users/billings/blackduck/runs/2022-03-07-20-46-29-611
````

Upload each of the output files (.bdio and .bdmu files, found in subdirectories of the run directory) into [blackduck_product_name] using the [blackduck_product_name] Scans page.
