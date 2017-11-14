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