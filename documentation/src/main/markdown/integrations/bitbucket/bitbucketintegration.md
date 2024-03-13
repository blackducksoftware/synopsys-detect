# Bitbucket integration
[company_name] [solution_name] consolidates several scanning utilities and tools and can be used to scan artifacts in a [Bitbucket pipeline](https://bitbucket.org/product/features/pipelines). The following procedures provide guidance on setting up [company_name] [solution_name] with your Bitbucket continuous integration builds.

## Prerequisites

Integration with BitBucket requires a fully configured instance of [company_name] [solution_name] and compatible instance of Java. For prerequisite information refer to [Requirements and release information](../../gettingstarted/requirements.md)

## Configuring with API tokens

The recommended way of configuring [company_name] [solution_name] with a Bitbucket pipeline is to use an API token.   

1. In [blackduck_product_name], navigate to the profile of the user whose credentials are used to scan projects from the pipeline.
2. Scroll down to the **User Access Token** section, and complete the fields to create a new token.
3. Check both the **Read Access** and **Write Access** boxes.
4. Click  **Generate.** Save or copy the displayed token.

    <figure>
    <img src="../bitbucket/images/myaccesstokens.png"
         alt="Creating an access token">
    <figcaption>Creating the access token</figcaption>
    </figure>
	
## Configuring [company_name] [solution_name] for Bitbucket with an API token

This section describes how to run [company_name] [solution_name] with Bitbucket pipelines using an API token. 

1.	On the project's Bitbucket page, navigate to **Settings** and then click **Repository Variables** in the left navigation under **Pipelines**.

	<figure>
    <img src="../bitbucket/images/xapitoken.png"
         alt="Configuring with an access token">
    <figcaption>Configuring the pipeline with an access token</figcaption>
    </figure>

2.	Create the following environment variables:

	- BLACKDUCK_URL - URL of your [blackduck_product_name] environment.

	- BLACKDUCK_TOKEN - API token that you generated in [blackduck_product_name].
	
3.	Add the following snippet to the `bitbucket-pipelines.yml` file:

```
bash <(curl -s -L https://detect.synopsys.com/detect9.sh) --blackduck.url="${BLACKDUCK_URL}" 
--blackduck.api.token="${BLACKDUCK_TOKEN}" --blackduck.trust.cert=true --<any other flags>
```

The resulting pipeline YAML file may appear with content similar to the following:

```
# This is a sample build configuration for Java (Maven).
# Check our guides at https://confluence.atlassian.com/x/zd-5Mw for more examples.
# Only use spaces to indent your .yml configuration.
# -----
# You can specify a custom docker image from Docker Hub as your build environment.
image: maven:3.3.9
  
pipelines:
  default:
    - step:
        caches:
          - maven
        script: # Modify the commands below to build your repository.
          - mvn -B verify # -B batch mode makes Maven less verbose
          - mvn clean package
        artifacts:
          - target/**
    - step:
        name: synopsys-detect
        script:
          - bash <(curl -s -L https://detect.synopsys.com/detect9.sh) --blackduck.url="${BLACKDUCK_URL}" --blackduck.api.token="${BLACKDUCK_TOKEN} --blackduck.trust.cert=true"
```

<note type="important">Configure [company_name] [solution_name] as a command after the code-build step as it relies on access to the code tree and the build environment.</note>

When you commit the modified YAML file, the build is triggered. After the pipeline build with [company_name] [solution_name] completes, you can view the complete scan results in your [blackduck_product_name] instance. For additional information and properties for [company_name] [solution_name], refer to [Detect properties](../../properties/all-properties.md) for more details.
 	
