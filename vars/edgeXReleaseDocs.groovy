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

/**
 # edgeXReleaseDocs

 ## Overview

 Shared library with helper functions to manage documentation releases. Currently used by edgex-docs.

 ## Required Yaml

 Name | Required | Type | Description and Default Value
 -- | -- | -- | --
 docs | true | str | Determines whether or not to trigger this function. |
 docsInfo.nextReleaseVersion | true | str | Next release version to add to the documentation. |
 docsInfo.nextReleaseName | true | str | Next release name to add to the documentation. |
 docsInfo.reviewers | true | str | Who to assign the generated PR's to. |

 ## Functions
 - `edgeXReleaseDocs.publishReleaseBranch`: Makes release branch related changes in unique branch then commits release branch.
 - `edgeXReleaseDocs.publishVersionChangesPR`: Makes version file related changes in unique branch then commits and opens PR.
 - `edgeXReleaseDocs.validate`: Validates release yaml input before any automation is run.
 
 ## Usage

 ### Sample Release Yaml

 ```yaml
 name: 'edgex-docs'
 version: '2.2.0'
 releaseName: 'kamakura'
 releaseStream: 'main'
 repo: 'https://github.com/edgexfoundry/edgex-docs.git'
 commitId: 'c72b16708d6eed9a08be464a432ce22db7d90667'
 gitTag: false
 dockerImages: false
 docs: true
 docsInfo:
   nextReleaseVersion: "2.3.0"
   nextReleaseName: levski
   reviewers: edgex-docs-committers
 ```

 # Groovy Call

 ```groovy
 edgeXReleaseDocs(releaseYaml)
 ```
*/

// For this function DRY_RUN is treated differently. For dry run here
// it is helpful to see all the modification steps happening with the
// exception of actually pushing the changes or opening a PR.
def call (releaseInfo) {
    validate(releaseInfo)

    // if we are release docs, git tag is false so no clone is explicitly called
    // so we need to clone here
    withEnv(['DRY_RUN=false']) {
        edgeXReleaseGitTag.cloneRepo(
            releaseInfo.repo,
            releaseInfo.releaseStream,
            releaseInfo.name,
            releaseInfo.commitId,
            'edgex-jenkins-ssh'
        )
    }

    // set user info for commits
    sh 'git config --global user.email "jenkins@edgexfoundry.org"'
    sh 'git config --global user.name "EdgeX Jenkins"'

    dir(releaseInfo.name) {
        publishReleaseBranch(releaseInfo)
        publishVersionChangesPR(releaseInfo)
    }
}

def publishReleaseBranch(releaseInfo) {
    def branchName = releaseInfo.releaseName

    sh "git checkout ${releaseInfo.commitId}"
    sh "git checkout -b ${branchName}"

    // make changes
    sh 'rm -rf docs'

    edgex.bannerMessage "[edgeXReleaseDocs] Here is the diff related release branch changes"
    sh 'git status'

    edgex.commitChange("ci: Automated changes for [${branchName}] release")

    // only push the change when we are not doing a DRY_RUN
    if(edgex.isDryRun()) {
        echo "git push origin ${branchName}"
    } else {
        sshagent(credentials: ['edgex-jenkins-ssh']) {
            sh "git push origin ${branchName}"
        }
    }
}

def publishVersionChangesPR(releaseInfo) {
    def branch = "${releaseInfo.releaseName}-version-changes"
    sh "git reset --hard ${releaseInfo.commitId}"
    sh "git checkout -b ${branch}"

    def currentVersion = releaseInfo.version.substring(0, releaseInfo.version.lastIndexOf('.'))

    // index.html change
    println("[edgeXReleaseDocs] Updating 'latest' version alias")

    // versions.json change
    def nextVersion     = releaseInfo.docsInfo.nextReleaseVersion.substring(0, releaseInfo.docsInfo.nextReleaseVersion.lastIndexOf('.'))
    def nextReleaseName = releaseInfo.docsInfo.nextReleaseName.capitalize()

    // versions.1 = remove latest, versions.2 new version
    // remove current version alias
    sh "jq 'map((select(.aliases[0] == \"latest\") | .aliases) |= [])' docs/versions.json | tee docs/versions.1"

    // set latest alias for release version
    sh "jq 'map((select(.version == \"${currentVersion}\") | .aliases) |= [\"latest\"])' docs/versions.1 | tee docs/versions.2"

    println("[edgeXReleaseDocs] Adding next version to version json")

    def versionAddScript = "jq '. += [{\"version\": \"${nextVersion}\", \"title\": \"${nextVersion}-${nextReleaseName}\", \"aliases\": []}]' docs/versions.2"
    def newVersionJson = sh(script: versionAddScript, returnStdout: true)

    // this is to keep the current JSON syntax (could change in the future)
    newVersionJson = newVersionJson.replaceAll(/\n    /, ' ').replaceAll(/\[   /, "[ ").replaceAll(/\n  \}/, ' }')

    // override the existing version json
    writeFile(file: 'docs/versions.json', text: newVersionJson)

    // clean up temp files
    sh 'rm -f docs/versions.1 docs/versions.2'

    println("[edgeXReleaseDocs] Creating new version macro file")
    def newMacroFile = createVersionMacroFile(releaseInfo.version, releaseInfo.releaseName, releaseInfo.docsInfo.nextReleaseVersion)
    writeFile(file: 'template_macros.yaml', text: newMacroFile)

    println("[edgeXReleaseDocs] Updating release version mkdocs.yml")

    sh "sed -i 's|${currentVersion}|${nextVersion}|g' mkdocs.yml"

    edgex.bannerMessage "[edgeXReleaseDocs] Here is the diff related to the version changes"
    sh 'git diff'

    def title = "ci: automated version file changes for [${nextVersion}]"
    def body = "This PR updates the version files to the next release version ${nextVersion}"

    edgex.createPR(branch, title, body, releaseInfo.docsInfo.reviewers)
}

def createVersionMacroFile(releaseVersion, releaseName, nextVersion) {
    """
    latest_released_version: '${releaseVersion}'
    latest_release_name: '${releaseName}'
    next_version: '${nextVersion}'
    """.stripIndent().replaceAll("(?m)^[ \t]*\r?\n", "")
}

def validate(releaseYaml) {
    if(releaseYaml.gitTag) {
        error("[edgeXReleaseDocs] Cannot publish docs and use gitTag at the same time. Please set gitTag to false")
    }

    if(!releaseYaml.releaseStream) {
        error("[edgeXReleaseDocs] Release yaml does not contain 'releaseStream' (branch where you are releasing from). Example: main")
    }

    if(!releaseYaml.version) {
        error("[edgeXReleaseDocs] Release yaml does not contain release 'version'. Example: 2.2.0")
    }

    if(!releaseYaml.docsInfo.nextReleaseName) {
        error("[edgeXReleaseDocs] Release yaml does not contain 'docsInfo.nextReleaseName'. This is required to update the next version information. Example: levski")
    }

    if(!releaseYaml.docsInfo.nextReleaseVersion) {
        error("[edgeXReleaseDocs] Release yaml does not contain release 'docsInfo.nextReleaseVersion'. Example: 2.3.0")
    }
}
