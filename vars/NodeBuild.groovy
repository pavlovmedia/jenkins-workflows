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
    }
  }
}