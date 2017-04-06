def call() {
    def version
    if (fileExists("version.json")) {
      def versionText = readFile("version.json")
      def versionJson = new groovy.json.JsonSlurper().parseText(versionText)
      version = versionJson?.version
      println "Read version as ${version}"
    }
    
    stage("Docker build")
    def repo = "nexus.dev.pavlovmedia.corp:5000"
    def imagename = "spa-ranch"
    
    sh "/usr/bin/docker build --no-cache -t ${imagename} ."
    if (env.BRANCH_NAME.matches(/.*-release$/)) {
      if (!version || version.matches(/.*-SNAPSHOT$/)) {
        throw new IllegalStateException("Release builds must have a valid version")
      }
      stage("Docker tag")
      sh "/usr/bin/docker tag ${imagename} ${repo}/${imagename}:${version}"
      
      stage("Docker push (aka deploy)")
      sh "/usr/bin/docker push ${repo}/${imagename}:${version}"
    }
}