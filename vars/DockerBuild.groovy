// Does a full java 8 build
def call(imageName, repo="dockerhub.pavlovmedia.net") {
  // Run all this in a single node
  node("docker") {
    NotifyWrapper {
      stage ("Project checkout") {
        checkout scm
      }

    def version
    stage("Version Check") {
    if (fileExists("version.json")) {
      def versionText = readFile("version.json")
      def versionJson = new groovy.json.JsonSlurper().parseText(versionText)
      version = versionJson?.version
      }
      println "Read version as ${version}"
    }

      // Do the build
      DockerBuildStep(imageName, version, repo)
    }
  }
}