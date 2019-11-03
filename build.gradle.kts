import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.jfrog.bintray.gradle.BintrayExtension
import com.jfrog.bintray.gradle.tasks.BintrayUploadTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.KotlinSourceSet
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

val SourceSet.kotlin: SourceDirectorySet
    get() = withConvention(KotlinSourceSet::class) { kotlin }

fun BintrayExtension.pkg(configure: BintrayExtension.PackageConfig.() -> Unit) {
    pkg(delegateClosureOf(configure))
}

plugins {
    `java-library`
    `maven-publish`
    kotlin("jvm") version "1.3.50"
    id("com.github.ben-manes.versions") version "0.27.0"
    id("com.jfrog.bintray") version "1.8.4"
    id("net.researchgate.release") version "2.8.1"
    id("org.jetbrains.dokka") version "0.10.0"
    id("org.jetbrains.kotlin.plugin.spring") version "1.3.50"
}

repositories {
    mavenCentral()
    jcenter()
    maven(url = "https://dl.bintray.com/michaelbull/maven")
}

dependencies {
    api("com.michael-bull.kotlin-result:kotlin-result:1.1.3")
    implementation(kotlin("stdlib"))
    implementation("javax.inject:javax.inject:1")
    implementation("org.springframework.boot:spring-boot-starter-web:2.2.0.RELEASE")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.2.0.RELEASE")
}

tasks.withType<DependencyUpdatesTask> {
    rejectVersionIf {
        listOf("alpha", "beta", "rc", "cr", "m", "eap", "pr").any {
            candidate.version.contains(it, ignoreCase = true)
        }
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xuse-experimental=kotlin.contracts.ExperimentalContracts")
    }
}

tasks.withType<Test> {
    failFast = true
    useJUnitPlatform()
}

val dokka by tasks.existing(DokkaTask::class) {
    outputFormat = "javadoc"
    outputDirectory = "$buildDir/docs/javadoc"
}

val javadocJar by tasks.registering(Jar::class) {
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Assembles a jar archive containing the Javadoc API documentation."
    archiveClassifier.set("javadoc")
    dependsOn(dokka)
    from(dokka.get().outputDirectory)
}

val sourcesJar by tasks.registering(Jar::class) {
    group = LifecycleBasePlugin.BUILD_GROUP
    description = "Assembles a jar archive containing the main classes with sources."
    archiveClassifier.set("sources")
    from(project.the<SourceSetContainer>().getByName("main").allSource)
}

publishing {
    publications {
        register("mavenJava", MavenPublication::class) {
            from(components["java"])
            artifact(javadocJar.get())
            artifact(sourcesJar.get())
        }
    }
}

val bintrayUser: String? by project
val bintrayKey: String? by project

bintray {
    user = bintrayUser
    key = bintrayKey
    setPublications("mavenJava")

    pkg {
        repo = "maven"
        name = project.name
        desc = project.description
        websiteUrl = "https://github.com/michaelbull/spring-boot-starter-recaptcha"
        issueTrackerUrl = "https://github.com/michaelbull/spring-boot-starter-recaptcha-retry/issues"
        vcsUrl = "git@github.com:michaelbull/spring-boot-starter-recaptcha.git"
        githubRepo = "michaelbull/spring-boot-starter-recaptcha"
        setLicenses("ISC")
    }
}

val bintrayUpload by tasks.existing(BintrayUploadTask::class) {
    dependsOn("build")
    dependsOn("generatePomFileForMavenJavaPublication")
    dependsOn(sourcesJar)
    dependsOn(javadocJar)
}

tasks.named("afterReleaseBuild") {
    dependsOn(bintrayUpload)
}
