// Does a basic node build
def call() {
    stage("npm install") {
        sh "npm install"
    }

    stage ("gulp build") {
        sh "gulp build --production"
    }
}