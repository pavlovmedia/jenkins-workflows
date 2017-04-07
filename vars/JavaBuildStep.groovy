def call(branchName, prefix="Java", pom="./pom.xml", doObr=true) {

  def mvnHome = tool 'Maven3'
  stage ("${prefix} build") {
    // Set up to do a full build, release or otherwise with deploys if applicable
    def mvnTargets = "clean install javadoc:aggregate checkstyle:checkstyle"
    
    if (branchName.endsWith("-release")) {
      mvnTargets = "-X clean install javadoc:aggregate checkstyle:checkstyle source:jar javadoc:jar deploy -DaltDeploymentRepository=release.builder.dev.pavlovmedia.corp::default::http://release.builder.dev.pavlovmedia.corp/nexus/content/repositories/releases/ -Ddocker.repo='nexus.dev.pavlovmedia.corp:5000'"
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
      stage ("${prefix} Deploy") {
        sh "${mvnHome}/bin/mvn -s settings/Builders/settings.xml source:jar javadoc:jar deploy -DskipTests=true -DaltDeploymentRepository=release.builder.dev.pavlovmedia.corp::default::http://release.builder.dev.pavlovmedia.corp/nexus/content/repositories/releases/ -Ddocker.repo='nexus.dev.pavlovmedia.corp:5000'"
      }

      if (doObr) {
        stage ("${prefix} OBR Deploy") {
          sh "${mvnHome}/bin/mvn -s settings/Builders/settings.xml deploy -DaltDeploymentRepository=obr.dev.pavlovmedia.corp::default::http://obr.dev.pavlovmedia.corp/maven/pavlov"
        }
      }
    }
  }
}
