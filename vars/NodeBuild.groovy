// Does a full java 8 build
def call(nodeName="node") {
  // Run all this in a single node
  node("${nodeName}") {
    echo "Running on ${nodeName}"
    JavaNotifyWrapper {
      stage ("Project checkout") {
        checkout scm
      }

      NodeBuildStep()
    }
  }
}