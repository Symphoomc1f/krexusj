import com.homeaway.devtools.jenkins.testing.JenkinsPipelineSpecification
import spock.lang.Ignore

public class EdgeXReleaseDockerImageIntSpec extends JenkinsPipelineSpecification {
    def edgeXReleaseDockerImage, edgex
    def validReleaseYaml, invalidReleaseYaml
    
    public static class TestException extends RuntimeException {
        public TestException(String _message) {
            super( _message );
        }
    }

    def setup() {
        edgeXReleaseDockerImage = loadPipelineScriptForTest('vars/edgeXReleaseDockerImage.groovy')

        def edgeXDocker = loadPipelineScriptForTest('vars/edgeXDocker.groovy')
        edgeXReleaseDockerImage.getBinding().setVariable('edgeXDocker', edgeXDocker)

        edgex = loadPipelineScriptForTest('vars/edgex.groovy')
        edgeXReleaseDockerImage.getBinding().setVariable('edgex', edgex)

        explicitlyMockPipelineVariable('out')

        validReleaseYaml = [
            name:'app-functions-sdk-go',
            version:'v1.2.0',
            releaseName:'geneva',
            releaseStream:'master',
            repo:'https://github.com/edgexfoundry/app-functions-sdk-go.git',
            dockerImages:true,
            docker:[[
                image: 'nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go',
                destination: [
                    'nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go',
                    'edgexfoundry/docker-app-functions-sdk-go'
                ]
            ], [
                image: 'nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64',
                destination: [
                    'nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go-arm64',
                    'edgexfoundry/docker-app-functions-sdk-go-arm64'
                ]
            ]]
        ]

        invalidReleaseYaml = [
            name:'app-functions-sdk-go',
            version:'v1.2.0',
            releaseName:'geneva',
            releaseStream:'master',
            repo:'https://github.com/edgexfoundry/app-functions-sdk-go.git',
            dockerImages:true,
            docker:[[
                image: 'nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go',
                destination: [
                    'nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go', // invalid destination
                    'example.com/edgexfoundry/docker-app-functions-sdk-go' // invalid destination
                ]
            ]]
        ]
    }

    def "Test edgeXReleaseDockerImage [Should] run echo commands to tag and push when DRY_RUN is true [When] called" () {
        setup:
            def environmentVariables = [
                'DRY_RUN': 'true',
                'RELEASE_DOCKER_SETTINGS': 'some-settings'
            ]
            edgeXReleaseDockerImage.getBinding().setVariable('env', environmentVariables)
            edgex.getBinding().setVariable('env', environmentVariables)

        when:

            try {
                edgeXReleaseDockerImage.publishDockerImages(validReleaseYaml)
            } catch(TestException exception) { }
        then:
            1 * getPipelineMock('echo').call("[edgeXReleaseDockerImage] DRY_RUN: docker login happens here")

            1 * getPipelineMock('echo').call([
                    'docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master',
                    'docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go:v1.2.0',
                    'docker push nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go:v1.2.0'
                ].join("\n"))

            1 * getPipelineMock('echo').call([
                    'docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master',
                    'docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master docker.io/edgexfoundry/docker-app-functions-sdk-go:v1.2.0',
                    'docker push docker.io/edgexfoundry/docker-app-functions-sdk-go:v1.2.0'
                ].join('\n'))

            1 * getPipelineMock('echo').call([
                    'docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master',
                    'docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go-arm64:v1.2.0',
                    'docker push nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go-arm64:v1.2.0'
                ].join("\n"))

            1 * getPipelineMock('echo').call([
                    'docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master',
                    'docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master docker.io/edgexfoundry/docker-app-functions-sdk-go-arm64:v1.2.0',
                    'docker push docker.io/edgexfoundry/docker-app-functions-sdk-go-arm64:v1.2.0'
                ].join('\n'))
    }

    def "Test edgeXReleaseDockerImage [Should] run echo commands to tag and push when DRY_RUN_PULL_DOCKER_IMAGES is true [When] called" () {
        setup:
            def environmentVariables = [
                'DRY_RUN': 'true',
                'DRY_RUN_PULL_DOCKER_IMAGES': 'true',
                'RELEASE_DOCKER_SETTINGS': 'some-settings'
            ]
            edgeXReleaseDockerImage.getBinding().setVariable('env', environmentVariables)
            edgex.getBinding().setVariable('env', environmentVariables)

        when:

            try {
                edgeXReleaseDockerImage.publishDockerImages(validReleaseYaml)
            } catch(TestException exception) { }
        then:
            1 * getPipelineMock('echo').call("[edgeXReleaseDockerImage] DRY_RUN: docker login happens here")

            2 * getPipelineMock('sh').call("docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master")

            1 * getPipelineMock('echo').call([
                    'docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master',
                    'docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go:v1.2.0',
                    'docker push nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go:v1.2.0'
                ].join("\n"))

            1 * getPipelineMock('echo').call([
                    'docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master',
                    'docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master docker.io/edgexfoundry/docker-app-functions-sdk-go:v1.2.0',
                    'docker push docker.io/edgexfoundry/docker-app-functions-sdk-go:v1.2.0'
                ].join('\n'))

            2 * getPipelineMock('sh').call("docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master")

            1 * getPipelineMock('echo').call([
                    'docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master',
                    'docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go-arm64:v1.2.0',
                    'docker push nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go-arm64:v1.2.0'
                ].join("\n"))

            1 * getPipelineMock('echo').call([
                    'docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master',
                    'docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master docker.io/edgexfoundry/docker-app-functions-sdk-go-arm64:v1.2.0',
                    'docker push docker.io/edgexfoundry/docker-app-functions-sdk-go-arm64:v1.2.0'
                ].join('\n'))
    }

    def "Test edgeXReleaseDockerImage [Should] run sh commands to tag and push when DRY_RUN is false [When] called" () {
        setup:
            def environmentVariables = [
                'DRY_RUN': 'false',
                'RELEASE_DOCKER_SETTINGS': 'some-settings'
            ]
            edgeXReleaseDockerImage.getBinding().setVariable('env', environmentVariables)
            edgex.getBinding().setVariable('env', environmentVariables)

            explicitlyMockPipelineVariable('edgeXDockerLogin')

        when:
            try {
                edgeXReleaseDockerImage.publishDockerImages(validReleaseYaml)
            } catch(TestException exception) { }
        then:
            1 * getPipelineMock('edgeXDockerLogin.call')(settingsFile: 'some-settings')

            2 * getPipelineMock('sh').call("docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master")

            1 * getPipelineMock('sh').call("docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go:v1.2.0")
            1 * getPipelineMock('sh').call("docker push nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go:v1.2.0")
            1 * getPipelineMock('sh').call("docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master docker.io/edgexfoundry/docker-app-functions-sdk-go:v1.2.0")
            1 * getPipelineMock('sh').call("docker push docker.io/edgexfoundry/docker-app-functions-sdk-go:v1.2.0")

            2 * getPipelineMock('sh').call("docker pull nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master")

            1 * getPipelineMock('sh').call("docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go-arm64:v1.2.0")
            1 * getPipelineMock('sh').call("docker push nexus3.edgexfoundry.org:10002/docker-app-functions-sdk-go-arm64:v1.2.0")
            1 * getPipelineMock('sh').call("docker tag nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go-arm64:master docker.io/edgexfoundry/docker-app-functions-sdk-go-arm64:v1.2.0")
            1 * getPipelineMock('sh').call("docker push docker.io/edgexfoundry/docker-app-functions-sdk-go-arm64:v1.2.0")

            2 * getPipelineMock('echo').call("[edgeXReleaseDockerImage] Successfully published [2] images")
    }

    def "Test edgeXReleaseDockerImage [Should] error [When] invalid yaml configuration is used" () {
        setup:
            def environmentVariables = [
                    'DRY_RUN': 'false'
            ]
            edgeXReleaseDockerImage.getBinding().setVariable('env', environmentVariables)
            edgex.getBinding().setVariable('env', environmentVariables)

            explicitlyMockPipelineVariable('edgeXDockerLogin')

        when:
            try {
                edgeXReleaseDockerImage.publishDockerImages(invalidReleaseYaml)
            } catch(TestException exception) { }
        then:
            1 * getPipelineMock('echo').call("[edgeXReleaseDockerImage] The sourceImage [nexus3.edgexfoundry.org:10004/docker-app-functions-sdk-go:master] did not release...")
    }

}