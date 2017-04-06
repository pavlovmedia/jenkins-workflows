// Does a node build
def call() {
  // Run all this in a single node
  node("node") {
    NotifyWrapper {
      stage ("Project checkout") {
        checkout scm
      }

      NodeBuildStep()

      // docker if we have it
      if (fileExists("Dockerfile")) {
        def packageText = readFile("package.json")
        def packageJson = new groovy.json.JsonSlurper().parseText(packageText)        
        def version = packageJson?.version
        def imageName = packageJson?.name
        packageJson = null
        if (version && imageName) {
          println "Read version as ${version}, image as ${imageName}"
          DockerBuildStep(imageName, version)
        }
      }
    }
  }
}