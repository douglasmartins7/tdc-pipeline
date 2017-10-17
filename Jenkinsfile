pipeline {
    agent any
    stages {
        /*stage('Checkout') {
            steps {
                git 'https://github.com/rmpestano/tdc-cars.git'
                lastChanges format:'SIDE',matching: 'LINE'
            }
        }
        */
        stage('build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('unit-tests') {
            steps {
                sh 'mvn test'
            }
        }

        stage('it-tests') {
            steps {
                sh 'mvn flyway:clean flyway:migrate -Pmigrations -Ddb.name=cars-test'
                sh 'mvn test -Pit-tests -Darquillian.port-offset=100 -Darquillian.port=10090'
                livingDocs()
            }
        }

        stage('ft-tests') {
            steps {
                sh 'mvn flyway:clean flyway:migrate -Pmigrations -Ddb.name=cars-ft-test'
                sh 'mvn test -Pft-tests -Darquillian.port-offset=120 -Darquillian.port=10110'
            }
        }

        stage('migrations') {
            steps {
                sh 'mvn flyway:repair flyway:migrate -P migrations'
            }
        }

        stage('deploy') {
            steps {
                sh 'docker stop tdc-cars || true && docker rm tdc-cars || true'
                sh 'docker build -t rmpestano/tdc-cars .'
                sh 'docker run --name tdc-cars -p 8181:8080 -v ~/db:/opt/jboss/db tdc-cars &'
            }
        }

        stage('smoke-tests') {
            steps {
                sh 'mvn test -Psmoke -DAPP_CONTEXT=http://localhost:8181/tdc-cars/rest/health'
            }
        }

        stage('perf-tests') {
            steps {
                sh 'mvn gatling:execute -Pperf -DAPP_CONTEXT=http://localhost:8181/tdc-cars/'
                gatlingArchive()
            }
        }
    }
}