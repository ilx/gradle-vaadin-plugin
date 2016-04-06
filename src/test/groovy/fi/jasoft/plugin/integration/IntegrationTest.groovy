/*
* Copyright 2016 John Ahlroos
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package fi.jasoft.plugin.integration

import groovy.transform.PackageScope
import org.gradle.testkit.runner.GradleRunner
import org.junit.Before
import org.junit.Rule
import org.junit.rules.TemporaryFolder

import java.nio.file.Paths

/**
 * Base class for integration tests
 */
class IntegrationTest {

    @Rule
    public TemporaryFolder projectDir = new TemporaryFolder()

    protected File buildFile

    @Before
    void setup() {
        buildFile = projectDir.newFile("build.gradle")
        def projectVersion = System.getProperty('integrationTestProjectVersion')

        def libsDir = Paths.get('.', 'build', 'libs').toFile()
        def escapedDir = libsDir.canonicalPath.replace("\\","\\\\")

        // Apply plugin to project
        buildFile << """
            buildscript {
                repositories {
                    mavenCentral()
                    flatDir dirs: file('$escapedDir')
                }

                dependencies {
                    classpath group: 'org.codehaus.groovy.modules.http-builder', name: 'http-builder', version: '0.7.1'
                    classpath group: 'fi.jasoft.plugin', name: 'gradle-vaadin-plugin', version: '$projectVersion'
                }
            }

            repositories {
                flatDir dirs: file('$escapedDir')
            }

            apply plugin: fi.jasoft.plugin.GradleVaadinPlugin

            vaadin.logToConsole = true
        """
    }

    protected String runWithArguments(String... args) {
        setupRunner().withArguments((args as List) + ['--stacktrace']).build().output
    }

    protected String runFailureExpected() {
        setupRunner().buildAndFail().output
    }

    protected GradleRunner setupRunner() {
        GradleRunner.create().withProjectDir(projectDir.root)
    }
}