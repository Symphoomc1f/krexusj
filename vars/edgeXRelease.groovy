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

def collectReleaseYamlFiles(filePath = 'release/*.yaml', releaseBranch = 'release') {
    def releaseData = []
    def yamlFiles = findFiles(glob: filePath)

    for (f in yamlFiles) {
        if (edgex.didChange(f.toString(), releaseBranch)) {
            releaseData << readYaml(file: f.toString())
        }   
    }

    return releaseData
}

def parallelStepFactory(data) {
    return data.collectEntries {
        ["${it.name}" : parallelStepFactoryTransform(it)]
    }
}

def parallelStepFactoryTransform(step) {
return {
        stage(step.name.toString()) {

            if(step.gitTag == true) {
                stage("Git Tag Publish") {

                    edgeXReleaseGitTag(step)

                }
            }
            
            if(step.dockerImages == true) {
                stage("Docker Image Publish") {
                    echo "[edgeXRelease] about to release docker images for ${step.dockerSource.join(',')}"
                    // This looping logic may make more sense in the edgeXReleaseDockerImage library
                    for(int i = 0; i < step.dockerSource.size(); i++) {
                        def dockerFrom = step.dockerSource[i]
                        def dockerFromClean = dockerFrom.replaceAll('https://', '')
                        // assumes from always has hostname
                        def dockerFromImageName = dockerFromClean.split('/').last().split(':').first()

                        for(int j = 0; j < step.dockerDestination.size(); j++) {
                            def dockerTo = step.dockerDestination[j]
                            def dockerToClean = dockerTo.replaceAll('https://', '')
                            // assumes from always has hostname
                            def dockerToImageName = dockerToClean.split('/').last()

                            if(dockerFromImageName == dockerToImageName) {
                                edgeXReleaseDockerImage (
                                    from: dockerFrom,
                                    to: dockerTo,
                                    version: step.version
                                )
                            }
                        }
                    }
                }
            }

            if(step.snap == true) {
                stage("Snap Publish") {
                    
                    edgeXReleaseSnap(step)

                }
            }
        }
    }
}