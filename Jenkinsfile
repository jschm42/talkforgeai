node {
  stage('SCM') {
    checkout scm
  }
  stage('SonarQube Analysis') {
    def mvn = tool 'Maven 3.9.8';
    withSonarQubeEnv() {
      sh "${mvn}/bin/mvn clean verify sonar:sonar -Dsonar.projectKey=TalkforgeAI -Dsonar.projectName='TalkforgeAI'"
    }
  }
}
