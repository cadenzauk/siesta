/*
 * Copyright (c) 2017-2025 Cadenza United Kingdom Limited
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
    java
    `java-library`
    `java-test-fixtures`
    `maven-publish`
    signing
}

group = "com.cadenzauk"
version = libs.versions.siesta.get()

repositories {
    mavenCentral()
}

dependencies {
    api(libs.hibernateJpa21Api)
    api(libs.jetbrainsAnnotations)
    api(libs.apacheCommonsLang3)
    api(libs.googleGuava)
    api(libs.slf4jApi)

    implementation(libs.objenesis)

    testFixturesApi(libs.springContext) {
        exclude(group = "commons-logging")
    }
    testFixturesApi(libs.springTest) {
        exclude(group = "commons-logging")
    }
    testFixturesApi(libs.springJdbc) {
        exclude(group = "commons-logging")
    }
    testFixturesApi(libs.hamcrest)
    testFixturesApi(libs.mockitoCore)
    testFixturesApi(libs.mockitoJunitJupiter)
    testFixturesApi(libs.java8Matchers)
    testFixturesApi(libs.junitJupiterApi)
    testFixturesApi(libs.junitJupiterParams)
    testFixturesApi(libs.h2)
    testFixturesApi(libs.jacksonDatabind)
    testFixturesApi(libs.jacksonDatatypeJdk8)

    testFixturesRuntimeOnly(libs.jclOverSlf4j)

    testImplementation(libs.hamcrestDate)
    testImplementation(libs.hsqldb)
    testImplementation(libs.derby)
    testImplementation(libs.derbytools)
    testImplementation(libs.googleGuavaTestlib)
    testImplementation(libs.jaxbImpl)
    testImplementation(libs.logbackClassic)
    testImplementation(libs.junitJupiterEngine)

    testRuntimeOnly(libs.junitPlatformLauncher)
}

tasks.test {
    useJUnitPlatform {
        excludeEngines("junit-vintage")
    }
}

java {
    targetCompatibility = JavaVersion.VERSION_17
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

with ((components.findByName("java") as AdhocComponentWithVariants)) {
    withVariantsFromConfiguration(configurations.testFixturesApiElements.get()) { skip() }
    withVariantsFromConfiguration(configurations.testFixturesRuntimeElements.get()) { skip() }
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
                name = "Cadenza UK SIESTA"
                artifactId = "siesta"
                packaging = "jar"
                description = "SIESTA Is an Easy SQL Typesafe API"
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
