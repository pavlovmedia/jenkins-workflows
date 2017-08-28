// Does a basic node build
def call() {
  stage("npm install") {
      sh "npm install"
  }

  if (fileExists("gulpfile.js") || fileExists("Gulpfile.js")) {
    stage("gulp build") {
        sh "gulp build --production"
    }
  }
  
  /** Don't do npm releases
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
  **/
}