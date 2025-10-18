pipeline {
    agent any

    environment {
        MVN_HOME = '/usr/share/maven'                                 // Maven installation path
        SONAR_TOKEN = credentials('sonar-token')                      // SonarQube token
        NEXUS_CREDS = credentials('nexus-user')                       // Nexus credentials (username/password)
        NEXUS_REPO = 'http://54.164.115.219:8081/repository/maven-releases/'  // Nexus repo URL
        DEPLOY_USER = 'ubuntu'                                        // SSH user on deploy server
        DEPLOY_HOST = '54.164.14.146'                                 // Deploy server IP
        SONAR_HOST = 'http://54.80.62.172:9000'                      // SonarQube URL
        JAR_FILE = 'my-app-1.0.0.jar'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/manisha0418/my-app.git'
            }
        }

        stage('Build') {
            steps {
                sh "${MVN_HOME}/bin/mvn clean package -B"
            }
        }

        stage('Unit Tests') {
            steps {
                sh "${MVN_HOME}/bin/mvn test -B"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                sh """
                    ${MVN_HOME}/bin/mvn sonar:sonar \
                    -Dsonar.login=${SONAR_TOKEN} \
                    -Dsonar.host.url=${SONAR_HOST} -B
                """
            }
        }

        stage('Publish to Nexus') {
            steps {
                sh """
                    ${MVN_HOME}/bin/mvn deploy -B \
                    -DaltDeploymentRepository=nexus::default::http://${NEXUS_CREDS_USR}:${NEXUS_CREDS_PSW}@54.164.115.219:8081/repository/maven-releases/
                """
            }
        }

        stage('Deploy Artifact') {
            steps {
                sshagent(['deploy-server-key']) {
                    sh '''
ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} << 'EOF'
  mkdir -p ~/deployments/my-app
  cd ~/deployments/my-app
  curl -f -O http://54.164.115.219:8081/repository/maven-releases/com/example/springboot-hello/1.0/springboot-hello-1.0.jar
  pkill -f ${JAR_FILE} || true
  nohup java -jar springboot-hello-1.0.jar > app.log 2>&1 &
EOF
                    '''
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline completed successfully!"
        }
        failure {
            echo "Pipeline failed. Check logs for errors."
        }
        always {
            echo "Pipeline finished."
        }
    }
}
