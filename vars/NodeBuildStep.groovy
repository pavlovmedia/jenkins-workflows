// Does a basic node build
def call() {
  environment {
    PATH = "/home/dev/.nvm/versions/node/v6.9.5/bin:$PATH"
  }
  stage("npm install") {
      sh "npm cache clear"
      sh "rm -rf node_modules"
      sh "node --version"
      sh "npm install"
  }

  stage("build") {
    sh "npm run build"
  }
  
  if (!fileExists("Dockerfile")) {
    def version
    def versionText = readFile("package.json")
    def versionJson = new groovy.json.JsonSlurper().parseText(versionText)
    version = versionJson?.version
    println "Read version as ${version}"
    versionJson=""

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