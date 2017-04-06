def call() {
pipeline {
    agent {
        docker { image 'node:4.8.2-alpine' }
    }
    stages {
        stage('Test') {
            steps {
                sh 'node --version'
            }
        }
    }
}
}