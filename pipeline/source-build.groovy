pipeline {
    agent { label 'maven' }
    options {
        buildDiscarder(logRotator(numToKeepStr: '5'))
        timeout(time: 10, unit: 'MINUTES')
    }
    environment {
        DEFAULT_JVM_OPTS = "-Dhttp.proxyHost=l98fppx1.admin.arbeitslosenkasse.ch -Dhttp.proxyPort=8080 -Dhttps.proxyHost=l98fppx1.admin.arbeitslosenkasse.ch -Dhttps.proxyPort=8080"
        JAVA_TOOL_OPTIONS = "$JAVA_TOOL_OPTIONS $DEFAULT_JVM_OPTS"
		NEXUS = credentials('nexus-admin')
    }
    stages {
        stage('prepare') {
            steps {
                git branch: 'build', poll: false, url: 'https://github.com/chrira/example-spring-boot-helloworld.git'
            }
        }
        stage('Build App') {
            steps {
                sh './gradlew build'
				sh "curl --fail -u \'${NEXUS}\' --upload-file build/libs/springboots2idemo-0.0.1-SNAPSHOT.jar http://nexus-build-infra.apps.admin.arbeitslosenkasse.ch/repository/maven-snapshots/ch/admin/test/upload/0.1-SNAPSHOT/upload-0.1-SNAPSHOT.jar"
            }
            post {
                always {
                    junit 'build/test-results/*.xml'  // Requires JUnit plugin
                }
                success {
                    archiveArtifacts 'build/libs/springboots2idemo*.jar'
                }
            }
        }
	stage('Build') {
            steps {
		    script {
		      openshift.withCluster() {
			openshift.withProject() {
				openshift.raw('start-build example-spring-boot-helloworld --from-dir=. --follow')
				
			def rubySelector = openshift.selector("bc", "example-spring-boot-helloworld")
                    	def builds
               
                        rubySelector.object()
                        builds = rubySelector.related( "builds" )
                        
                        
                       	builds.untilEach {
				return it.object().status.phase == "Complete" || it.object().status.phase == "Failed" || it.object().status.phase == "Cancelled" || it.object().status.phase == "Aborted" 
			    }
				
                         echo "Build logs for ${builds.names()}:"
				
			// Find the bc again, and ask for its logs
                    def result = rubySelector.logs()
    
                    // Each high-level operation exposes stout/stderr/status of oc actions that composed
                    echo "Result of logs operation:"
                    echo "  status: ${result.status}"
                    echo "  stderr: ${result.err}"
                    echo "  number of actions to fulfill: ${result.actions.size()}"
                    echo "  first action executed: ${result.actions[0].cmd}"
				
				/*
			  def builds = openshift.startBuild(".", "--from-build=example-spring-boot-helloworld")
			  builds.logs('-f')
			  timeout(15) {
			    builds.untilEach(1) {
			      return (it.object().status.phase == "Complete")
			    }
			  }
				*/
			}
		      }
		    }
	    }
	}
    }
}
