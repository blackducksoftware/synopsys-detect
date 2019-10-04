# Java Bazel example

##### Build:

    bazel build //pipelineChooser/samples/bazel/java:hello-world
    bazel build //pipelineChooser/samples/bazel/java:hello-resources
    
##### Run:

    bazel run //pipelineChooser/samples/bazel/java:hello-world
    bazel run //pipelineChooser/samples/bazel/java:hello-resources
    
##### Test:

###### All:

    bazel test //pipelineChooser/samples/bazel/java/...

###### Specific:

    bazel test //pipelineChooser/samples/bazel/java:hello
    bazel test //pipelineChooser/samples/bazel/java:custom
