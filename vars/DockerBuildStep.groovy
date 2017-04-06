def call(imagename, version) {
  stage("Docker build") {
    sh "/usr/bin/docker build --no-cache -t ${imagename} ."

    if (env.BRANCH_NAME.matches(/.*-release$/)) {
      if (!version || version.matches(/.*-SNAPSHOT$/)) {
        throw new IllegalStateException("Release builds must have a valid version")
      }

      stage("Docker tag") {
        sh "/usr/bin/docker tag ${imagename} ${repo}/${imagename}:${version}"
      }
      
      stage("Docker push (aka deploy)") {
        sh "/usr/bin/docker push ${repo}/${imagename}:${version}"
      }
    }
  }
}