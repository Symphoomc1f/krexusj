//
// Copyright (c) 2020 Intel Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

def call(Map config = [:]) {
    def dryRun = ['1', 'true'].contains(env.DRY_RUN)
    def repoUrl = config.repoUrl ?: null
    def credentialId = config.credentialId ?: 'edgex-jenkins-ssh'
    def ghPagesBranch = config.ghPagesBranch ?: 'gh-pages'
    def stashName = config.stashName ?: 'site-contents'

    if (!repoUrl){
        error("[edgeXGHPagesPublish]: Repository URL missing in config")
    }

    try{
        cleanWs()
        dir('gh-pages-src') {
            git url: "${repoUrl}", branch: "${ghPagesBranch}", credentialsId: "${credentialId}", changelog: false,
                poll: false
            unstash "${stashName}"
            withEnv(["DRY_RUN=${dryRun}",
                     "GH_PAGES_BRANCH=${ghPagesBranch}"
            ]) {
                sshagent(credentials: ["${credentialId}"]) {
                    sh(script: libraryResource('github-pages-publish.sh'))
                }
            }
        }
    }
    catch (Exception ex){
        error("[edgeXGHPagesPublish]: ERROR occurred when publishing to GH Pages: ${ex}")
    }
}