pipeline {
    agent { label 'maven' }
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout(time: 10, unit: 'MINUTES')
    }
    environment {
        DEFAULT_JVM_OPTS = "-Dhttp.proxyHost=l98fppx1.admin.arbeitslosenkasse.ch -Dhttp.proxyPort=8080 -Dhttps.proxyHost=l98fppx1.admin.arbeitslosenkasse.ch -Dhttps.proxyPort=8080"
        JAVA_TOOL_OPTIONS = "$JAVA_TOOL_OPTIONS $DEFAULT_JVM_OPTS"
    }
    stages {
        stage('prepare') {
            steps {
                git branch: 'build', poll: false, url: 'https://github.com/chrira/example-spring-boot-helloworld.git'
            }
        }
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
    }
}
