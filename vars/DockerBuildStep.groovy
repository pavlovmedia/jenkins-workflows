def call(imageName, version) {
  def repo = "dockerhub.pavlovmedia.net"
  stage("Docker build") {
    sh "/usr/bin/docker build --no-cache -t ${imageName} ."

    if (env.BRANCH_NAME.matches(/.*-release$/)) {
      if (!version || version.matches(/.*-.*$/)) {
        throw new IllegalStateException("Release builds must have a valid version")
      }

      stage("Docker deploy") {
        sh "/usr/bin/docker tag ${imageName} ${repo}/${imageName}:${version}"
        sh "/usr/bin/docker push ${repo}/${imageName}:${version}"
      }
    }
  }
}