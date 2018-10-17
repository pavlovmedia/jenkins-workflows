// Does a node build
def call(nodeName="node") {
  // Run all this in a single node
  node("${nodeName}") {
    NotifyWrapper {
      stage ("Project checkout") {
        checkout scm
      }

      // Does a basic node build
      stage("npm install") {
        sh "rm -rf node_modules target"
        sh "npm --version"
        sh "npm install"
      }

      stage("build") {
        sh "npm run build"
      }

      def packageText = readFile("package.json");
      def packageJson = new groovy.json.JsonSlurper().parseText(packageText)
      version = packageJson?.version
      println "Read version as ${version}"
      def distDirectory = packageJson?.ngPackage?.dest
      if (!distDirectory) {
        // If no destination directory is provided, ngPackage defaults to 'dist'.
        distDirectory = "dist"
      }
      packageJson = "" // jsonSlurper is non-serializable and if we move to another stage with it the build will fail.

      stage("npm deploy") {
        if (env.BRANCH_NAME.matches(/.*-release$/)) {
          if (!version || version.matches(/.*-.*$/)) {
            throw new IllegalStateException("Release builds must have a valid version")
          }
          println "Packaging and publishing the files in ${distDirectory}"
          sh "cd ${distDirectory}; npm publish"
        }
      }

      stage("cleanup") {
        packageJson = ""
        sh "rm -rf ./node_modules"
      }
    }
  }
}
