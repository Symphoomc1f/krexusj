/*edgeXGeneric([
    project: 'edgex-go',
    mavenSettings: ['edgex-go-codecov-token:CODECOV_TOKEN'],
    env: [
        GOPATH: '/opt/go-custom/go'
    ],
    path: [
        '/opt/go-custom/go/bin'
    ],
    branches: [
        '*': [
            pre_build: ['shell/install_custom_golang.sh'],
            build: [
                'make test raml_verify && make build docker',
                'shell/codecov-uploader.sh'
            ]
        ],
        'master': [
            post_build: [ 'shell/edgexfoundry-go-docker-push.sh' ]
        ]
    ]
])*/

def cfgAmd64
def cfgArm64

def call(config) {
    edgex.bannerMessage "[edgeXGeneric] RAW Config: ${config}"

    validate(config)
    edgex.setupNodes(config)

    def _envVarMap = toEnvironment(config)

    pipeline {
        agent { label edgex.mainNode(config) }

        options {
            timestamps()
            preserveStashes()
            quietPeriod(5) // wait a few seconds before starting to aggregate builds...??
            durabilityHint 'PERFORMANCE_OPTIMIZED'
            timeout(360)
        }

        stages {
            stage('Prepare') {
                steps {
                    edgeXSetupEnvironment(_envVarMap)

                    dir('.ci-management') {
                        git url: 'https://github.com/edgexfoundry/ci-management.git'
                    }

                    stash name: 'ci-management', includes: '.ci-management/**', useDefaultExcludes: false
                }
            }

            stage('Semver Prep') {
                when { environment name: 'USE_SEMVER', value: 'true' }
                steps {
                    edgeXSemver 'init' // <-- Generates a VERSION file and .semver directory
                }
            }
            
            stage('Build') {
                parallel {
                    stage('amd64') {
                        when { expression { edgex.nodeExists(config, 'amd64') } }
                        agent { label edgex.getNode(config, 'amd64') }
                        environment {
                            ARCH = 'x86_64'
                        }
                        stages {
                            stage('Prep VM') {
                                steps {
                                    edgeXDockerLogin(settingsFile: env.MAVEN_SETTINGS)
                                    unstash 'ci-management'
                                    script {
                                        cfgAmd64 = getConfigFilesFromEnv()
                                    }
                                }
                            }
                            stage('Pre Build') {
                                when { expression { anyScript(config, 'pre_build', env.GIT_BRANCH) } }
                                steps {
                                    script {
                                        configFileProvider(cfgAmd64) {
                                            withEnv(["PATH=${setupPath(config)}"]) {
                                                def scripts = allScripts(config, 'pre_build', env.GIT_BRANCH)
                                                println "$ARCH pre_build: ${scripts}"
                                                scripts.each { userScript ->
                                                    if(userScript.indexOf('shell/') == 0) {
                                                        sh "sh .ci-management/${userScript}"
                                                    } else {
                                                        sh userScript
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            stage('Build') {
                                when { expression { anyScript(config, 'build', env.GIT_BRANCH) } }
                                steps {
                                    script {
                                        configFileProvider(cfgAmd64) {
                                            withEnv(["PATH=${setupPath(config)}"]) {
                                                def scripts = allScripts(config, 'build', env.GIT_BRANCH)
                                                println "$ARCH build: ${scripts}"
                                                scripts.each { userScript ->
                                                    if(userScript.indexOf('shell/') == 0) {
                                                        sh "sh .ci-management/${userScript}"
                                                    } else {
                                                        sh userScript
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            stage('Post Build') {
                                when { expression { anyScript(config, 'post_build', env.GIT_BRANCH) } }
                                steps {
                                    script {
                                        configFileProvider(cfgAmd64) {
                                            withEnv(["PATH=${setupPath(config)}"]) {
                                                def scripts = allScripts(config, 'post_build', env.GIT_BRANCH)
                                                println "$ARCH post_build: ${scripts}"
                                                scripts.each { userScript ->
                                                    if(userScript.indexOf('shell/') == 0) {
                                                        sh "sh .ci-management/${userScript}"
                                                    } else {
                                                        sh userScript
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    stage('arm64') {
                        when { expression { edgex.nodeExists(config, 'arm64') } }
                        agent { label edgex.getNode(config, 'arm64') }
                        environment {
                            ARCH = 'arm64'
                        }
                        stages {
                            stage('Prep VM') {
                                steps {
                                    edgeXDockerLogin(settingsFile: env.MAVEN_SETTINGS)
                                    unstash 'ci-management'
                                    script {
                                        cfgArm64 = getConfigFilesFromEnv()
                                    }
                                }
                            }
                            stage('Pre Build') {
                                when { expression { anyScript(config, 'pre_build', env.GIT_BRANCH) } }
                                steps {
                                    script {
                                        configFileProvider(cfgArm64) {
                                            withEnv(["PATH=${setupPath(config)}"]) {
                                                def scripts = allScripts(config, 'pre_build', env.GIT_BRANCH)
                                                println "$ARCH pre_build: ${scripts}"
                                                scripts.each { userScript ->
                                                    if(userScript.indexOf('shell/') == 0) {
                                                        sh "sh .ci-management/${userScript}"
                                                    } else {
                                                        sh userScript
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            stage('Build') {
                                when { expression { anyScript(config, 'build', env.GIT_BRANCH) } }
                                steps {
                                    script {
                                        configFileProvider(cfgArm64) {
                                            withEnv(["PATH=${setupPath(config)}"]) {
                                                def scripts = allScripts(config, 'build', env.GIT_BRANCH)
                                                println "$ARCH build: ${scripts}"
                                                scripts.each { userScript ->
                                                    if(userScript.indexOf('shell/') == 0) {
                                                        sh "sh .ci-management/${userScript}"
                                                    } else {
                                                        sh userScript
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                            stage('Post Build') {
                                when { expression { anyScript(config, 'post_build', env.GIT_BRANCH) } }
                                steps {
                                    script {
                                        configFileProvider(cfgArm64) {
                                            withEnv(["PATH=${setupPath(config)}"]) {
                                                def scripts = allScripts(config, 'post_build', env.GIT_BRANCH)
                                                println "$ARCH post_build: ${scripts}"
                                                scripts.each { userScript ->
                                                    if(userScript.indexOf('shell/') == 0) {
                                                        sh "sh .ci-management/${userScript}"
                                                    } else {
                                                        sh userScript
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            stage('Semver') {
                when {
                    allOf {
                        environment name: 'USE_SEMVER', value: 'true'
                        expression { edgex.isReleaseStream() }
                    }
                }
                stages {
                    stage('Tag') {
                        steps {
                            unstash 'semver'

                            edgeXSemver 'tag'
                            edgeXInfraLFToolsSign(command: 'git-tag', version: 'v${VERSION}')
                        }
                    }
                    stage('Bump Pre-Release Version') {
                        steps {
                            edgeXSemver 'bump pre'
                            edgeXSemver 'push'
                        }
                    }
                }
            }
        }
    }

    // email notification: EdgeX-Jenkins-Alerts+int+444+7674852109629482390@lists.edgexfoundry.org Jenkins Mailer PLugin
    // Send e-mail for every unstable build
}

def validate(config) {
    if(!config.project) {
        error('[edgeXGeneric] The parameter "project" is required. This is typically the project name.')
    }
}

def toEnvironment(config) {
    def _projectName   = config.project
    def _projectSettings = "${_projectName}-settings:SETTINGS_FILE"

    def _defaultSettings = config.mavenSettings ?: [ _projectSettings ]

    // rebuild maven settings array
    def _mavenSettings
    def _extraSettings = []

    _defaultSettings.each { setting ->
        def settingName = setting.split(':')[0]
        def settingEnvVar = setting.split(':')[1]

        if(setting == _projectSettings) {
            if(env.SILO == 'sandbox') {
                _mavenSettings = 'sandbox-settings'
            } else {
                _mavenSettings = settingName
            }
        }
        else {
            _extraSettings << setting
        }
    }

    def _useSemver = edgex.defaultFalse(config.semver)

    def envMap = [
        MAVEN_SETTINGS: _mavenSettings,
        EXTRA_SETTINGS: _extraSettings.join(','),
        PROJECT: _projectName,
        USE_SEMVER: _useSemver
    ]

    if(config.env) {
        envMap << config.env
    }

    edgex.bannerMessage "[edgeXGeneric] Pipeline Parameters:"
    edgex.printMap envMap

    envMap
}

@NonCPS
def getScripts(config, scriptType, branch) {
    def scripts = []
    def cleanBranch = branch.replaceAll(/origin|upstream/, '').replaceAll('/', '')
    def branches = config.branches.findAll { k,v ->  (k == cleanBranch) }

    branches.each { b,v ->
        if(v && v[scriptType]) {
            scripts.addAll(v[scriptType])
        }
    }

    scripts
}

@NonCPS
def anyScript(config, scriptType, branch) {
    allScripts(config, scriptType, branch) ? true : false
}

@NonCPS
def allScripts(config, scriptType, branch) {
    def s = getScripts(config, scriptType, '*') ?: []

    if(branch) {
        s.addAll(getScripts(config, scriptType, branch))
    }

    s
}

@NonCPS
def getConfigFilesFromEnv() {
    def configFiles = []
    if(env.EXTRA_SETTINGS) {
        configFiles = env.EXTRA_SETTINGS.split(',').collect { file ->
            configFile(fileId: file.split(':')[0], variable: file.split(':')[1])
        }
    }

    configFiles
}

@NonCPS
def setupPath(config) {
    config.path ? "${PATH}:${config.path.join(':')}" : "${PATH}"
}