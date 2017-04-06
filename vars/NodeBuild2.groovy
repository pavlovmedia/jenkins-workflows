// 'node:4.8.2-alpine'
def call() {
// Run all this in a single node
 docker.image("node:4.8.2-alpine").inside {
    echo "Running on ${nodeName}"
    NotifyWrapper {
      stage ("Project checkout") {
        checkout scm
      }

      NodeBuildStep()
    }
  }
}