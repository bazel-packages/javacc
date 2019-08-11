workspace(name = "org_javacc")

load("@bazel_tools//tools/build_defs/repo:http.bzl", "http_archive")

http_archive(
    name = "io_bazel",
    strip_prefix = "bazel-0.28.1",
    urls = [
        "https://github.com/bazelbuild/bazel/archive/0.28.1.tar.gz",
    ],
)

http_archive(
    name = "rules_java",
    strip_prefix = "rules_java-master",
    urls = [
        "https://github.com/bazelbuild/rules_java/archive/master.tar.gz",
    ],
)

load("@rules_java//java:repositories.bzl", "rules_java_dependencies", "rules_java_toolchains")
rules_java_dependencies()
rules_java_toolchains()
