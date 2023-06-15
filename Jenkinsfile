def  ms_name = scm.getUserRemoteConfigs()[0].getUrl().tokenize('/')[3].split("\\.")[0]
def  temp_name = ms_name.split("-v1")[0];
def latestVersion = 0;
properties([
  parameters([
    string(name: 'imageTag', defaultValue: '1'),
    choice(name: 'jobType', choices: ['BuildAndDeploy', 'Deploy']),
    choice(name: 'environment', choices: ['dev', 'prod']),
    choice(name: 'region', choices: ['eu-central-1', 'us-east-1']),
    choice(name: 'springProfile', choices: ['dev', 'prod', 'stage']),
    choice(name: 'dontStopCurrentTask', choices: ['no', 'yes'])
    ])
  ])


if (params.jobType == 'Deploy'){
  latestVersion = imageTag
}
def serviceName = ms_name;
def containerName = temp_name;
def repo_name = ms_name;
def taskD = ms_name;
def logs_location = params.environment+'/'+ms_name;
//def springProfile = params.environment;
if(ms_name == 'shipping-ms-v1' || ms_name == 'payments-ms-v1' || ms_name == 'users-ms-v1' || ms_name == 'store-ms-v1' || ms_name == 'ordering-ms-v1'){
  logs_location = springProfile;
}

if(springProfile == 'prod'){
  repo_name = ms_name+"_"+params.environment
  taskD = ms_name+"_"+params.environment
}

pipeline {
  agent { label 'master' }
  stages {
  stage('init') {
      steps {
          script{
            println(ms_name)
             if (params.jobType == 'BuildAndDeploy'){
             stage('create repo if not Exists') {

                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'bttrm-backend-ecr']]) {
                   sh " aws ecr describe-repositories --repository-names "+repo_name+" --region "+params.region+" ||aws ecr create-repository --repository-name "+repo_name+" --region "+params.region
                   script {
                      def currRepoTags = sh (
                      returnStdout: true,
                      script:  "                                                                                  \
                        aws ecr describe-images --repository-name ${repo_name} --region ${params.region}            \
                        --query 'sort_by(imageDetails,& imagePushedAt)[-1].imageTags[0]'"
                      ).trim()

                      if(currRepoTags == "null"){
                          latestVersion = 1
                      }else{
                          latestVersion = currRepoTags.split("\"")[1].split("\"")[0].toInteger()+1
                      }
                      //latestVersion = "latest"
                }
              }
            }
            stage('mvn clean install') {
                  sh "mvn clean install"
            }
            stage('Build image') {
               try{
                  sh "docker build --no-cache --build-arg APP_NAME="+repo_name+" -t 166826642036.dkr.ecr.eu-central-1.amazonaws.com/"+repo_name+":${latestVersion} -f Dockerfile ."
               } catch (err) {
                echo err
               }
           }
            stage('Push image') {
               script {
                  docker.withRegistry('https://166826642036.dkr.ecr.eu-central-1.amazonaws.com', 'ecr:eu-central-1:bttrm-backend-ecr') {
                      sh "docker push 166826642036.dkr.ecr.eu-central-1.amazonaws.com/"+repo_name+":${latestVersion}"
                }
              }
            }
            stage('clean built image') {
                  sh "docker image rm 166826642036.dkr.ecr.eu-central-1.amazonaws.com/"+repo_name+":${latestVersion} "
            }
            stage('create log group') {
               withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'bttrm-backend-ecr']]) {
                    try{
                        sh "echo aws logs create-log-group --log-group-name /ecs/${logs_location} --region ${params.region} "
                        sh "aws logs create-log-group --log-group-name /ecs/${logs_location} --region ${params.region} "
                    } catch (err) {
                        echo "log already exists"
                    }
                }
            }
          }
        }
      }
     }
    stage('Deploy') {
      steps {
        withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'bttrm-backend-ecr']]) {
            sh "sed -e  's;%ms_name%;${repo_name};g'                                                       \
                  aws/task-definition.json >                                                              \
                  aws/task-definition-1.json  "

            sh "sed -e  's;%SPRING_PROFILES_ACTIVE%;${springProfile};g'                                                       \
                  aws/task-definition-1.json >                                                              \
                  aws/task-definition-2.json  "

           sh "sed -e  's;%container_name%;${containerName};g'                                                       \
                  aws/task-definition-2.json >                                                              \
                  aws/task-definition-3.json  "

          sh "sed -e  's;%tag_version%;${latestVersion};g'                                                       \
                  aws/task-definition-3.json >                                                              \
                  aws/task-definition-4.json  "

          sh "sed -e  's;%logs_location%;${logs_location};g'                                                       \
                  aws/task-definition-4.json >                                                              \
                  aws/task-definition-5.json  "

          sh "sed -e  's;%logs_region%;${params.region};g'                                                       \
                  aws/task-definition-5.json >                                                              \
                  aws/task-definition-123.json  "

          script {
        sh "echo file://aws/task-definition-123.json"
        // get current Task def from ECS tasks
        def currTaskDef = sh (
              returnStdout: true,
              script:  "                                                                               \
                aws ecs describe-task-definition  --task-definition ${taskD} --region ${params.region}  \
                                              | egrep 'revision'                                       \
                                              | tr ',' ' '                                             \
                                              | awk '{print \$2}'                                      \
                "
            ).trim()

        //get all tasks with latest def from ecs task that are running
        def currentTask = sh (
                returnStdout: true,
                script:  "                                                                             \
                    aws ecs list-tasks  --cluster ${params.environment}                                 \
                                --family ${taskD}                                                    \
                                --output text --region ${params.region}                                 \
                                | egrep 'TASKARNS'                                                     \
                                | awk '{print \$2}'                                                    \
          "
        ).trim()


        sh "echo currTaskDef ${currTaskDef}"
        sh "echo currTaskDef ${currentTask}"

       //stop the running tasks
        if (params.dontStopCurrentTask == 'no' && currentTask) {
          sh "aws ecs stop-task --cluster ${params.environment} --task ${currentTask}  --region ${params.region}"
        }

        // Register the new [TaskDefinition]
        sh "                                                                                           \
          aws ecs register-task-definition  --family ${taskD}                                       \
                                            --cli-input-json file://aws/task-definition-123.json           \
                                             --region ${params.region}                                    \
        "

        // Get the last registered [TaskDefinition#revision]
        def taskRevision = sh (
          returnStdout: true,
          script:  "                                                                                   \
            aws ecs describe-task-definition  --task-definition ${taskD}  --region ${params.region}     \
                                              | egrep 'revision'                                       \
                                              | tr ',' ' '                                             \
                                              | awk '{print \$2}'                                      \
          "
        ).trim()

        // ECS update service to use the newly registered [TaskDefinition#revision]
        sh  "                                                                                         \
          aws ecs update-service  --cluster ${params.environment}                                        \
                                  --service ${serviceName}                                            \
                                  --task-definition ${taskD}:${taskRevision}                        \
                                  --desired-count 1   --region ${params.region}                          \
        "
          }
        }
      }
    }
  }
}
