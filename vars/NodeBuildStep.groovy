// Does a basic node build
def call() {
  stage("npm install") {
      sh "npm install"
  }

  stage("gulp build") {
      sh "gulp build --production"
  }

  if (!fileExists("Dockerfile")) {
    stage("npm deploy") {
      if (env.BRANCH_NAME.matches(/.*-release$/)) {
        if (!version || version.matches(/.*-.*$/)) {
            throw new IllegalStateException("Release builds must have a valid version")
        }
        sh "npm publish --registry https://nexus.dev.pavlovmedia.corp/repository/npm-hosted-release/"
      }
    }
  }
}