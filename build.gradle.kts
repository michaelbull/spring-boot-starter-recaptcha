import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.github.benmanes.gradle.versions.updates.gradle.GradleReleaseChannel
import com.vanniktech.maven.publish.JavadocJar
import com.vanniktech.maven.publish.KotlinJvm
import com.vanniktech.maven.publish.SourcesJar

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.kapt)
    alias(libs.plugins.kotlin.spring)
    alias(libs.plugins.versions)
    alias(libs.plugins.maven.publish)
}

description = "Spring Boot starter for Google's reCAPTCHA v3."

kotlin {
    jvmToolchain(17)

    compilerOptions {
        freeCompilerArgs.add("-Xannotation-default-target=param-property")
        freeCompilerArgs.add("-Xreturn-value-checker=full")
    }
}

dependencies {
    api(libs.kotlin.result)

    compileOnly(libs.jakarta.validation.api)
    compileOnly(libs.jakarta.servlet.api)

    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.jackson.annotations)
    implementation(libs.spring.web)
    implementation(libs.spring.boot.autoconfigure)
    implementation(libs.slf4j.api)

    kapt(platform(libs.spring.boot.dependencies))
    kapt(libs.spring.boot.configuration.processor)

    testImplementation(libs.spring.boot.starter.test)
    testImplementation(libs.jackson.databind)
    testImplementation(libs.jakarta.servlet.api)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<DependencyUpdatesTask> {
    gradleReleaseChannel = GradleReleaseChannel.CURRENT.id

    rejectVersionIf {
        listOf("alpha", "beta", "rc", "cr", "m", "eap", "pr", "dev").any {
            candidate.version.contains(it, ignoreCase = true)
        }
    }
}

tasks.withType<Test> {
    failFast = true
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest {
        attributes(mapOf("SPDX-License-Identifier" to "ISC"))
    }

    from(rootDir.resolve("LICENSE")) {
        into("META-INF")
    }
}

mavenPublishing {
    publishToMavenCentral()
    signAllPublications()

    configure(
        KotlinJvm(
            javadocJar = JavadocJar.Empty(),
            sourcesJar = SourcesJar.Sources(),
        ),
    )

    pom {
        name.set(project.name)
        description.set(project.description)
        url.set("https://github.com/michaelbull/spring-boot-starter-recaptcha")
        inceptionYear.set("2019")

        licenses {
            license {
                name.set("ISC License")
                url.set("https://opensource.org/licenses/isc-license.txt")
            }
        }

        developers {
            developer {
                name.set("Michael Bull")
                url.set("https://www.michael-bull.com")
            }
        }

        scm {
            connection.set("scm:git:https://github.com/michaelbull/spring-boot-starter-recaptcha")
            developerConnection.set("scm:git:git@github.com:michaelbull/spring-boot-starter-recaptcha.git")
            url.set("https://github.com/michaelbull/spring-boot-starter-recaptcha")
        }

        issueManagement {
            system.set("GitHub")
            url.set("https://github.com/michaelbull/spring-boot-starter-recaptcha/issues")
        }

        ciManagement {
            system.set("GitHub")
            url.set("https://github.com/michaelbull/spring-boot-starter-recaptcha/actions")
        }
    }
}
