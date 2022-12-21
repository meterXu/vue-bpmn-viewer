def gitUrl = "https://git.dpark.com.cn/git/sipsd-open-source/flowable_v4.git"
def branch = "master"
def credentialsId = "c078ac92-89dd-4842-8292-5e348e68db08"
def rootPath= "./sipsd-flowable-editor"
def appName = "flowable-editor"
def bakPath = "/home/jenkins_bak"
def putPath = "/home/jenkins_put"
def publishPath= "/usr/local/nginx/html/flowable-editor"
def robot = "360ac2ce-efe5-4478-8703-7cd3bd463044"

pipeline {
    agent any
    stages {
            stage('拉取代码') {
                steps {
                    git branch: "$branch", credentialsId: "$credentialsId", url: "$gitUrl"
                }
            }
            stage('项目编译') {
                steps {
                    script{
                        sh "cd ${rootPath} && yarn install && yarn run build"
                    }
                }
            }
            stage('项目部署') {
                steps {
                    script {
                         def remote = [:]
                         remote.name = 'remote'
                         remote.host ='192.168.126.25'
                         remote.user = 'root'
                         remote.password ='kjjsyf#0512'
                         remote.allowAnyHosts= true
                         stage ('备份') {
                            sshCommand remote: remote,command: "mkdir -p ${bakPath}/${appName}"
                            sshCommand remote: remote,command: "cp -r ${publishPath} ${bakPath}/${appName}/${BUILD_NUMBER}"
                         }
                         stage ('更新') {
                            sshCommand remote: remote,command: "mkdir -p ${putPath}/${appName}"
                            sshPut remote: remote,from: "${rootPath}/dist",into:"${putPath}/${appName}"
                         }
                         stage ('启动') {
                            sshCommand remote: remote,command: "rm -rf ${publishPath}"
                            sshCommand remote: remote,command: "mv ${putPath}/${appName}/dist ${publishPath}"
                         }
                    }
                }
            }
    }
    post {
        success {
            script {
                    dingtalk(robot: robot,
                                type: 'ACTION_CARD',
                                at: [],
                                atAll: false,
                                title: "",
                                text: [
                                "<font color=#0089ff >${appName}项目部署</font>",
                                "",
                                "----",
                                "",
                                "* 任务：#${BUILD_NUMBER}",
                                "* 项目：${appName}",
                                "* 状态：<font color=#00EE00 >部署成功</font>",
                                "* 持续时间：${currentBuild.durationString}".split("and counting")[0],
                                "* 执行人：${currentBuild.buildCauses.shortDescription}",
                                ],
                                messageUrl: '',
                                picUrl: '',
                                singleTitle: '',
                                btns: [],
                                btnLayout: 'H',
                                hideAvatar: false
                                )
            }
        }
        failure {
            script {
                    dingtalk(robot: robot,
                                type: 'ACTION_CARD',
                                at: [],
                                atAll: false,
                                title: "",
                                text: [
                                "<font color=#0089ff >${appName}项目部署</font>",
                                "",
                                "----",
                                "",
                                "* 任务：#${BUILD_NUMBER}",
                                "* 项目：${appName}",
                                "* 状态：<font color=#EE0000 >部署失败</font>",
                                "* 持续时间：${currentBuild.durationString}".split("and counting")[0],
                                "* 执行人：${currentBuild.buildCauses.shortDescription}",
                                ],
                                messageUrl: '',
                                picUrl: '',
                                singleTitle: '',
                                btnLayout: 'H',
                                hideAvatar: false
                                )
            }
        }
    }
}
