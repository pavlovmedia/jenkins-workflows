// Does a node build
def call(nodeName="node") {
  // Run all this in a single node
  node("${nodeName}") {
    echo "Running on ${nodeName}"
    NotifyWrapper {
      stage ("Project checkout") {
        checkout scm
      }

      NodeBuildStep()

      // docker if we have it
      if (fileExists("version.json") && fileExists("Dockerfile")) {
        // This was hard-coded in the original file
        def imagename = "spa-ranch"
        def versionText = readFile("version.json")
        def versionJson = new groovy.json.JsonSlurper().parseText(versionText)
        version = versionJson?.version
        println "Read version as ${version}"

        DockerBuildStep(imagename, version)
      }
    }
  }
}