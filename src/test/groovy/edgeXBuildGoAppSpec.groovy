import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification
import spock.lang.Ignore

public class EdgeXBuildGoAppSpec extends JenkinsPipelineSpecification {

    def edgeXBuildGoApp = null

    def setup() {
        edgeXBuildGoApp = loadPipelineScriptForTest('vars/edgeXBuildGoApp.groovy')

        explicitlyMockPipelineVariable('edgex')
    }

    def "Test prepBaseBuildImage [Should] call docker build with expected arguments [When] non ARM architecture" () {
        setup:
            def environmentVariables = [
                'ARCH': 'MyArch',
                'DOCKER_REGISTRY': 'MyDockerRegistry',
                'http_proxy': 'MyHttpProxy',
                'DOCKER_BASE_IMAGE': 'MyDockerBaseImage',
                'DOCKER_BUILD_FILE_PATH': 'MyDockerBuildFilePath',
                'DOCKER_BUILD_CONTEXT': 'MyDockerBuildContext'
            ]
            edgeXBuildGoApp.getBinding().setVariable('env', environmentVariables)
        when:
            edgeXBuildGoApp.prepBaseBuildImage()
        then:
             1 * getPipelineMock("docker.build").call([
                    'ci-base-image-MyArch',
                    '-f MyDockerBuildFilePath  --build-arg BASE=MyDockerBaseImage --build-arg http_proxy --build-arg https_proxy MyDockerBuildContext'])
    }

    def "Test prepBaseBuildImage [Should] call docker build with expected arguments [When] ARM architecture and base image contains registry" () {
        setup:
            def environmentVariables = [
                'ARCH': 'arm64',
                'DOCKER_REGISTRY': 'nexus3.edgexfoundry.org',
                'http_proxy': 'MyHttpProxy',
                'DOCKER_BASE_IMAGE': 'nexus3.edgexfoundry.org:10003/edgex-devops/edgex-golang-base:1.12.14-alpine',
                'DOCKER_BUILD_FILE_PATH': 'MyDockerBuildFilePath',
                'DOCKER_BUILD_CONTEXT': 'MyDockerBuildContext'
            ]
            edgeXBuildGoApp.getBinding().setVariable('env', environmentVariables)
        when:
            edgeXBuildGoApp.prepBaseBuildImage()
        then:
            1 * getPipelineMock('docker.build').call([
                    'ci-base-image-arm64', 
                    '-f MyDockerBuildFilePath  --build-arg BASE=nexus3.edgexfoundry.org:10003/edgex-devops/edgex-golang-base-arm64:1.12.14-alpine --build-arg http_proxy --build-arg https_proxy MyDockerBuildContext'])
    }

    def "Test validate [Should] raise error [When] config does not include a project parameter" () {
        setup:
        when:
            edgeXBuildGoApp.validate([:])
        then:
            1 * getPipelineMock('error').call('[edgeXBuildGoApp] The parameter "project" is required. This is typically the project name.')
    }

    def "Test toEnvironment [Should] return expected map of default values [When] sandbox environment" () {
        setup:
            def environmentVariables = [
                'SILO': 'sandbox'
            ]
            edgeXBuildGoApp.getBinding().setVariable('env', environmentVariables)
            getPipelineMock('edgex.defaultTrue').call(_) >> true
            getPipelineMock('edgex.defaultFalse').call(_) >> false
            getPipelineMock('edgex.getGoLangBaseImage').call(_) >> 'nexus3.edgexfoundry.org:10003/edgex-devops/edgex-golang-base:1.15-alpine'
        expect:
            edgeXBuildGoApp.toEnvironment(config) == expectedResult
        where:
            config << [
                [
                    project: 'pSoda'
                ]
            ]
            expectedResult << [
                [
                    MAVEN_SETTINGS: 'sandbox-settings',
                    PROJECT: 'pSoda',
                    USE_SEMVER: true,
                    TEST_SCRIPT: 'make test',
                    BUILD_SCRIPT: 'make build',
                    GO_VERSION: '1.15',
                    DOCKER_BASE_IMAGE: 'nexus3.edgexfoundry.org:10003/edgex-devops/edgex-golang-base:1.15-alpine',
                    DOCKER_FILE_PATH: 'Dockerfile',
                    DOCKER_BUILD_FILE_PATH: 'Dockerfile.build',
                    DOCKER_BUILD_CONTEXT: '.',
                    DOCKER_IMAGE_NAME: 'docker-pSoda',
                    DOCKER_REGISTRY_NAMESPACE: '',
                    DOCKER_NEXUS_REPO: 'staging',
                    BUILD_DOCKER_IMAGE: true,
                    PUSH_DOCKER_IMAGE: true,
                    BUILD_EXPERIMENTAL_DOCKER_IMAGE: false, 
                    BUILD_STABLE_DOCKER_IMAGE: false,
                    SEMVER_BUMP_LEVEL: 'pre',
                    GOPROXY: 'https://nexus3.edgexfoundry.org/repository/go-proxy/',
                    // SNAP_CHANNEL: 'latest/edge',
                    BUILD_SNAP: false,
                    PUBLISH_SWAGGER_DOCS: false,
                    SWAGGER_API_FOLDERS: 'openapi/v1'
                ]
            ]
    }

    def "Test toEnvironment [Should] return expected map of overriden values [When] non-sandbox environment and custom config" () {
        setup:
            getPipelineMock('edgex.defaultTrue').call(null) >> true
            getPipelineMock('edgex.defaultTrue').call(true) >> true
            getPipelineMock('edgex.defaultTrue').call(false) >> false
            getPipelineMock('edgex.defaultFalse').call(null) >> false
            getPipelineMock('edgex.defaultFalse').call(true) >> true
            getPipelineMock('edgex.defaultFalse').call(false) >> false
            getPipelineMock('edgex.getGoLangBaseImage').call(_) >> 'golang:MyGoVersion-alpine'
        expect:
            edgeXBuildGoApp.toEnvironment(config) == expectedResult
        where:
            config << [
                [
                    project: 'pSoda',
                    testScript: 'MyTestScript',
                    buildScript: 'MyBuildScript',
                    goVersion: 'MyGoVersion',
                    dockerFilePath: 'MyDockerFilePath',
                    dockerBuildFilePath: 'MyDockerBuildFilePath',
                    dockerBuildContext: 'MyDockerBuildContext',
                    dockerBuildArgs: ['MyArg1=Value1', 'MyArg2="Value2"'],
                    dockerNamespace: 'MyDockerNameSpace',
                    dockerImageName: 'MyDockerImageName',
                    dockerNexusRepo: 'MyNexusRepo',
                    buildExperimentalDockerImage: true,
                    semverBump: 'patch',
                    goProxy: 'https://www.example.com/repository/go-proxy/',
                    useAlpineBase: true,
                    // snapChannel: 'edge',
                    publishSwaggerDocs: true,
                    swaggerApiFolders: ['api/v20', 'api/v30']
                ]
            ]
            expectedResult << [
                [
                    MAVEN_SETTINGS: 'pSoda-settings',
                    PROJECT: 'pSoda',
                    USE_SEMVER: true,
                    TEST_SCRIPT: 'MyTestScript',
                    BUILD_SCRIPT: 'MyBuildScript',
                    GO_VERSION: 'MyGoVersion',
                    GOPROXY: 'https://www.example.com/repository/go-proxy/',
                    DOCKER_BASE_IMAGE: 'golang:MyGoVersion-alpine',
                    DOCKER_FILE_PATH: 'MyDockerFilePath',
                    DOCKER_BUILD_FILE_PATH: 'MyDockerBuildFilePath',
                    DOCKER_BUILD_CONTEXT: 'MyDockerBuildContext',
                    DOCKER_IMAGE_NAME: 'MyDockerImageName',
                    DOCKER_REGISTRY_NAMESPACE: 'MyDockerNameSpace',
                    DOCKER_NEXUS_REPO: 'MyNexusRepo',
                    BUILD_DOCKER_IMAGE: true,
                    PUSH_DOCKER_IMAGE: true,
                    BUILD_EXPERIMENTAL_DOCKER_IMAGE: true, 
                    BUILD_STABLE_DOCKER_IMAGE: false,
                    SEMVER_BUMP_LEVEL: 'patch',
                    // SNAP_CHANNEL: 'edge',
                    BUILD_SNAP: false,
                    PUBLISH_SWAGGER_DOCS: true,
                    SWAGGER_API_FOLDERS: 'api/v20 api/v30',
                    DOCKER_BUILD_ARGS: 'MyArg1=Value1,MyArg2="Value2"'
                ]
            ]
    }

}