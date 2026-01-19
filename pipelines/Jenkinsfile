pipeline {
    agent any

    environment {
        // Maven configuration
        MAVEN_HOME = '/usr/share/maven'
        PATH = "${MAVEN_HOME}/bin:${PATH}"
        JAVA_HOME = '/usr/lib/jvm/java-17-openjdk'
    }

    stages {
        stage('Checkout') {
            steps {
                echo '========== Checking out source code =========='
                checkout scm
                sh 'git log --oneline -5'
            }
        }

        stage('Build') {
            steps {
                echo '========== Building the project =========='
                sh 'mvn clean compile -DskipTests -q'
                echo '✓ Project built successfully'
            }
        }

        stage('Code Quality') {
            steps {
                echo '========== Running code quality checks =========='
                sh 'mvn checkstyle:check -q'
                sh 'mvn pmd:check -q'
                sh 'mvn spotbugs:check -q'
                echo '✓ Code quality checks passed'
            }
        }

        stage('Unit Tests') {
            steps {
                echo '========== Running unit tests =========='
                sh 'mvn test -q'
                echo '✓ Unit tests completed'
            }
        }

        stage('Generate Reports') {
            steps {
                echo '========== Generating test reports =========='
                sh 'mvn verify -DskipTests -q'
                echo '✓ Reports generated'
            }
        }

        stage('Code Coverage') {
            steps {
                echo '========== Collecting code coverage =========='
                sh 'mvn jacoco:report -q'
                publishHTML([
                    reportDir: 'target/site/jacoco',
                    reportFiles: 'index.html',
                    reportName: 'JaCoCo Code Coverage Report',
                    keepAll: true
                ])
                echo '✓ Code coverage report published'
            }
        }

        stage('Publish Test Results') {
            steps {
                echo '========== Publishing test results =========='
                junit 'target/surefire-reports/*.xml'
                publishHTML([
                    reportDir: 'target/cucumber-reports',
                    reportFiles: 'cucumber-report.html',
                    reportName: 'Cucumber Test Report',
                    keepAll: true
                ])
                echo '✓ Test results published'
            }
        }
    }

    post {
        always {
            echo '========== Cleaning up =========='
            cleanWs(deleteDirs: true, patterns: [[pattern: 'target/**', type: 'INCLUDE']])
        }

        success {
            echo '✓ Pipeline completed successfully!'
            mail to: '${BUILD_USER_EMAIL}',
                subject: "Build Successful: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: """
                    Build Status: SUCCESS
                    Job: ${env.JOB_NAME}
                    Build Number: ${env.BUILD_NUMBER}
                    Build URL: ${env.BUILD_URL}
                    
                    Reports:
                    - Cucumber Report: ${env.BUILD_URL}Cucumber_Test_Report/
                    - JaCoCo Coverage: ${env.BUILD_URL}JaCoCo_Code_Coverage_Report/
                """
        }

        failure {
            echo '✗ Pipeline failed!'
            mail to: '${BUILD_USER_EMAIL}',
                subject: "Build Failed: ${env.JOB_NAME} - ${env.BUILD_NUMBER}",
                body: """
                    Build Status: FAILURE
                    Job: ${env.JOB_NAME}
                    Build Number: ${env.BUILD_NUMBER}
                    Build URL: ${env.BUILD_URL}
                    
                    Please check the build logs for details.
                """
        }

        unstable {
            echo '⚠ Pipeline completed with warnings!'
        }
    }
}
