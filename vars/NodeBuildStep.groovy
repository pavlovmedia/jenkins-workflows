// Does a basic node build
def call() {
  stage("npm install") {
      sh "npm cache clear"
      sh "npm install"
  }

  stage("build") {
    sh "npm run build"
  }
  
  if (!fileExists("Dockerfile")) {
    stage("npm deploy") {
      if (env.BRANCH_NAME.matches(/.*-release$/)) {
        if (!version || version.matches(/.*-.*$/)) {
            throw new IllegalStateException("Release builds must have a valid version")
        }
        sh "npm publish"
      }
    }
  }
}