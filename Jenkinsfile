pipeline {
    agent any

    environment {
        MVN_HOME = '/usr/share/maven'
        SONAR_TOKEN = credentials('sonar-token')
        DEPLOY_USER = 'deployuser'
        DEPLOY_HOST = 'localhost'  // If deploying locally
        NEXUS_REPO = 'http://localhost:8081/repository/maven-releases/'
    }

    stages {

        stage('Checkout') {
            steps {
                git branch: 'main', url: 'https://github.com/your-repo/my-app.git'
            }
        }

        stage('Build') {
            steps {
                sh "${MVN_HOME}/bin/mvn clean package"
            }
        }

        stage('Unit Tests') {
            steps {
                sh "${MVN_HOME}/bin/mvn test"
            }
        }

        stage('SonarQube Analysis') {
            steps {
                sh "${MVN_HOME}/bin/mvn sonar:sonar -Dsonar.login=${SONAR_TOKEN}"
            }
        }

        stage('Publish to Nexus') {
            steps {
                sh """
                ${MVN_HOME}/bin/mvn deploy \
                -Dnexus.url=${NEXUS_REPO} \
                -Dnexus.username=NEXUS_USER \
                -Dnexus.password=NEXUS_PASSWORD
                """
            }
        }

        stage('Deploy Artifact') {
            steps {
                sshagent(['deploy-server-key']) {
                    sh """
                    ssh -o StrictHostKeyChecking=no ${DEPLOY_USER}@${DEPLOY_HOST} '
                        mkdir -p ~/deployments/my-app
                        cd ~/deployments/my-app
                        curl -O ${NEXUS_REPO}com/example/my-app/1.0.0/my-app-1.0.0.jar
                        java -jar my-app-1.0.0.jar &
                    '
                    """
                }
            }
        }
    }

    post {
        always {
            echo "Pipeline finished."
        }
    }
}

