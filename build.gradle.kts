/*
 * Copyright (c) 2017, 2025 Cadenza United Kingdom Limited
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
}

group = "com.cadenzauk"

repositories {
    mavenCentral()
}

tasks.register("integrationTest", Test::class) {
    description = "Runs integration tests."
    group = "verification"
    dependsOn(gradle.includedBuild("siesta-db2").task(":test"))
    dependsOn(gradle.includedBuild("siesta-firebird").task(":test"))
    dependsOn(gradle.includedBuild("siesta-mariadb").task(":test"))
    dependsOn(gradle.includedBuild("siesta-mysql").task(":test"))
    dependsOn(gradle.includedBuild("siesta-oracle").task(":test"))
    dependsOn(gradle.includedBuild("siesta-postgres").task(":test"))
    dependsOn(gradle.includedBuild("siesta-sqlserver").task(":test"))
}

tasks.test {
    dependsOn(gradle.includedBuild("siesta").task(":test"))
    dependsOn(gradle.includedBuild("siesta-kotlin").task(":test"))
    dependsOn(gradle.includedBuild("siesta-jackson").task(":test"))
}

tasks.clean {
    dependsOn(gradle.includedBuild("siesta").task(":clean"))
    dependsOn(gradle.includedBuild("siesta-kotlin").task(":clean"))
    dependsOn(gradle.includedBuild("siesta-jackson").task(":clean"))
    dependsOn(gradle.includedBuild("siesta-db2").task(":clean"))
    dependsOn(gradle.includedBuild("siesta-firebird").task(":clean"))
    dependsOn(gradle.includedBuild("siesta-mariadb").task(":clean"))
    dependsOn(gradle.includedBuild("siesta-mysql").task(":clean"))
    dependsOn(gradle.includedBuild("siesta-oracle").task(":clean"))
    dependsOn(gradle.includedBuild("siesta-postgres").task(":clean"))
    dependsOn(gradle.includedBuild("siesta-sqlserver").task(":clean"))
}

tasks.check {
    dependsOn("integrationTest")
}
