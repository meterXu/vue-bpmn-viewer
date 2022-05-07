def gitUrl = "http://192.168.126.25/git/cbb/flowable_v4.git"
def branch = "flowable_v4_pg"
def credentialsId = "c078ac92-89dd-4842-8292-5e348e68db08"
def packageName='沃壤工作流发版'
def versionLocal
def robot='flowable_publish'
def issuesUrl= 'http://issues.dpark.com.cn/git/issues/flowable_v4/-/issues'
def releasesUrl= 'http://192.168.126.25/git/cbb/flowable_v4/-/releases'
def docUrl= 'http://58.210.9.133/iplatform/geekdoc/docs/wow/wow-1deg8tivp0hhv'

// 删除tag：git tag -d v3.0.2
// 删除tag：git push origin :refs/tags/v3.0.2

pipeline {
    agent any
    stages {
            stage('拉取代码') {
                steps {
                    git branch: "$branch", credentialsId: "$credentialsId", url: "$gitUrl"
                }
            }
            stage('获取最新的tag') {
                steps {
                 script {
                    versionLocal = sh(script: "git describe --abbrev=0 --tags", returnStdout: true).trim()
                    sh(script:"echo ${versionLocal}")
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
                                    atAll: true,
                                    title: versionLocal,
                                    text: [
                                    "<font color=#0089ff >${packageName}</font>",
                                    "",
                                    "----",
                                    "",
                                    "* 任务：#${BUILD_NUMBER}",
                                    "* 名称：${packageName}",
                                    "* 版本：${versionLocal}",
                                    "* 状态：<font color=#00EE00 >发版成功</font>",
                                    "* 查看版本：[点击查看](${releasesUrl})",
                                    "* 查看issues：[点击查看](${issuesUrl})",
                                    "* 在线文档：[点击查看](${docUrl})",
                                    "* 持续时间：${currentBuild.durationString}".split("and counting")[0],
                                    "* 执行人：${currentBuild.buildCauses.shortDescription}",
                                    ],
                                    messageUrl: '',
                                    picUrl: '',
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
                                    title: versionLocal,
                                    text: [
                                    "<font color=#0089ff >${packageName}</font>",
                                    "",
                                    "----",
                                    "",
                                    "* 任务：#${BUILD_NUMBER}",
                                    "* 名称：${packageName}",
                                    "* 版本：${versionLocal}",
                                    "* 状态：<font color=#EE0000 >发版失败</font>",
                                    "* 持续时间：${currentBuild.durationString}".split("and counting")[0],
                                    "* 执行人：${currentBuild.buildCauses.shortDescription}",
                                    ],
                                    messageUrl: '',
                                    picUrl: '',
                                    btnLayout: 'H',
                                    hideAvatar: false
                                    )
                }
            }
        }
}
