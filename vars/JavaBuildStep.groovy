def call(branchName, prefix="Java", pom="./pom.xml", doLegacyObr=true, doObr2=false, obr2WebhookId="") {

  def mvnHome = tool 'Maven3'
  stage ("${prefix} build") {
    // Set up to do a full build, release or otherwise with deploys if applicable
    def mvnTargets = "clean install checkstyle:checkstyle"
    
    if (branchName.endsWith("-release")) {
      mvnTargets = "clean deploy -DaltDeploymentRepository=release.builder.dev.pavlovmedia.corp::default::http://release.builder.dev.pavlovmedia.corp/nexus/content/repositories/releases/ -Ddocker.repo='dockerhub.pavlovmedia.net'"
    }
    // Run the build
    sh "${mvnHome}/bin/mvn -s settings/Builders/settings.xml "+mvnTargets
  }

  stage ("Publish Jenkins Artifacts") {
    // Publish javadocs
    try {
      step([$class: 'JavadocArchiver', javadocDir: 'target/site/apidocs/'])
    } catch (err) { }
    
    try {  
      step([$class: 'WarningsPublisher', consoleParsers: [[parserName: 'Maven']]])
    } catch (err) { }

    // Use fully qualified hudson.plugins.checkstyle.CheckStylePublisher if JSLint Publisher Plugin or JSHint Publisher Plugin is installed
    try {
      step([$class: 'hudson.plugins.checkstyle.CheckStylePublisher', pattern: '**/target/checkstyle-result.xml'])
    } catch (err) { }

    // Deploy JUnit
    try {
      step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
    } catch (err) { }

    // Deploy this if we are on a release branch
    if (branchName.endsWith("-release")) {
      if (doLegacyObr) {
        // XXX: Docker won't run here as long as the plugin is 1.0.3+
        sh "${mvnHome}/bin/mvn -s settings/Builders/settings.xml deploy -DaltDeploymentRepository=obr.dev.pavlovmedia.corp::default::http://obr.dev.pavlovmedia.corp/maven/pavlov -DskipDocker=true"
      }

      if (doObr2 && obr2WebhookId) {
        postObr2Webhook(obr2WebhookId)
      }
    }
  }
}

def postObr2Webhook(obr2WebhookId) {
  // SSL is not working at this time -- javax.net.ssl.SSLHandshakeException is raised
  def conn = new URL("http://obr-rest.dev.pavlovmedia.corp/services/webhook/${obr2WebhookId}").openConnection()
  conn.setRequestMethod("POST")

  return conn.getResponseCode() == 200
}
