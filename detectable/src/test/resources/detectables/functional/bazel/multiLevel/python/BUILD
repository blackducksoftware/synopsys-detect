filegroup(
    name = "srcs",
    srcs = glob(["*.py"]) + [
        "BUILD",
        "//pipelineChooser/samples/bazel/python/fibonacci:srcs",
    ],
    visibility = ["//pipelineChooser/samples/bazel:__pkg__"],
)

py_binary(
    name = "bin",
    srcs = ["bin.py"],
    deps = [
        ":lib",
        "//pipelineChooser/samples/bazel/python/fibonacci",
    ],
)

py_library(
    name = "lib",
    srcs = ["lib.py"],
)

py_test(
    name = "test",
    srcs = ["test.py"],
    deps = [
        ":lib",
        "//pipelineChooser/samples/bazel/python/fibonacci",
    ],
)
