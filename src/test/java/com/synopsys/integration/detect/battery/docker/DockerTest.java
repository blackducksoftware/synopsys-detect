package com.synopsys.integration.detect.battery.docker;

public class DockerTest {
    public void withImage(String imageTag, String dockerfile) {

        dockerTest("detect-yarn-2:latest", "https://github.com/yarnpkg/berry.git", "yarn build")
        //    . assert ();

        //test clones the git url
        //pulls the detect image
        //mounts the pulled git project into the image
        //*swap* the built detect with the detect on the image
        //  open question: how to build the project/do we really want to clone the project to the local machine and not the docker image
        //      ideally we bake the cloning the building into the detect images so this could be a non-issue
        //  benefits; fewer docker files, less docker specific work, leveraging existing work, more reuse
        // reduse number of images,

        dockerTest("detect-specific-yarn-test:latest")
        //    . assert ();

        //standalone dockerfile that builds an image
        //project cloned and built 'in-image'
        //built detect mounted to image
        // benefits; isolated from non-test changes, no changes made to local machine

    }
}
