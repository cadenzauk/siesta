/*
 * Copyright (c) 2022-2025 Cadenza United Kingdom Limited
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
plugins {
    `java-library`
    `java-test-fixtures`
    `maven-publish`
    signing
}

group = "com.cadenzauk"
version = libs.versions.siesta.get()

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    api(libs.siesta)

    implementation(libs.jsonPath)
    implementation(libs.jsonSmart)
    implementation(libs.jacksonDatabind)
    implementation(libs.jacksonDatatypeJdk8)

    testFixturesImplementation(libs.jacksonDatabind)
    testFixturesImplementation(libs.jacksonDatatypeJdk8)

    testFixturesApi(testFixtures(libs.siesta))

    testImplementation(libs.hsqldb)
    testImplementation(libs.derby)
    testImplementation(libs.derbytools)

    testRuntimeOnly(libs.logbackClassic)
    testRuntimeOnly(libs.junitJupiterEngine)
    testRuntimeOnly(libs.junitPlatformLauncher)
}

tasks.test {
    useJUnitPlatform {
        excludeEngines("junit-vintage")
    }
}

val javadocJar = tasks.register<Jar>("javadocJar") {
    archiveClassifier.set("javadoc")
    from(tasks["javadoc"])
}

val sourcesJar = tasks.register<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets.main.get().allSource)
}

artifacts {
    add("archives", javadocJar)
    add("archives", sourcesJar)
}

publishing {
    repositories {
        maven {
            name = "ossrh"
            url = uri("https://oss.sonatype.org/service/local/staging/deploy/maven2")
            credentials {
                username = project.findProperty("ossrhUsername") as? String ?: ""
                password = project.findProperty("ossrhPassword") as? String ?: ""
            }
        }
    }

    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
            artifact(javadocJar.get())
            artifact(sourcesJar.get())

            pom {
                name.set("Cadenza UK SIESTA Jackson")
                artifactId = tasks.jar.get().archiveBaseName.get()
                packaging = "jar"
                description.set("SIESTA Is an Easy SQL Typesafe API with Jackson JSON Support")
                url = "https://github.com/cadenzauk/siesta"
                licenses {
                    license {
                        name = "The MIT License"
                        url = "https://raw.githubusercontent.com/cadenzauk/siesta/master/LICENCE"
                        distribution = "repo"
                    }
                }
                developers {
                    developer {
                        id = "mdrodg"
                        name = "Mark Rodgers"
                    }
                }
                scm {
                    url = "scm:git@github.com:cadenzauk/siesta.git"
                    connection = "scm:git@github.com:cadenzauk/siesta.git"
                    developerConnection = "scm:git@github.com:cadenzauk/siesta.git"
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["maven"])
}
