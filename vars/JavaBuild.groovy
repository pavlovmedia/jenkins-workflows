// Does a full java 8 build
def call(nodeName="java8", doObr=true) {
  // Run all this in a single node
  node("${nodeName}") {
    echo "Running on ${nodeName}"
    JavaNotifyWrapper {
      stage ("Project checkout") {
        checkout scm
      }

      // Get settings
      MavenSettingsStep()

      // Do the build
      JavaBuildStep(env.BRANCH_NAME, "Java", "./pom.xml", doObr)
    }
  }
}