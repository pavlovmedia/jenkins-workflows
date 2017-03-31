def call(prefix="Settings checkout") {
     def repo = env.SETTINGS_REPO ?: "http://gitlab.srv.pavlovmedia.corp/dev/maven-settings.git"
     stage ("${prefix} from ${repo}") {
     
     checkout scm: [$class: 'GitSCM', branches: [[name: '*/master']], 
        doGenerateSubmoduleConfigurations: false, 
        extensions: [[$class: 'RelativeTargetDirectory', relativeTargetDir: 'settings']], 
        submoduleCfg: [], userRemoteConfigs: [[url: "${repo}"]]]
     }
}