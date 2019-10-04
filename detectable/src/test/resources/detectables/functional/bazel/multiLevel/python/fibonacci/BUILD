filegroup(
    name = "srcs",
    srcs = glob(["*.py"]) + [
        "BUILD",
    ],
    visibility = ["//pipelineChooser/samples/bazel/python:__pkg__"],
)

py_library(
    name = "fibonacci",
    srcs = ["fib.py"],
    imports = ["."],
    visibility = ["//pipelineChooser/samples/bazel/python:__pkg__"],
)
