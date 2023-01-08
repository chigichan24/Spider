plugins {
    kotlin("jvm")
}

dependencies {
    implementation("com.google.devtools.ksp:symbol-processing-api:1.7.21-1.0.8")
    implementation(project(":spider-annotation"))
    testImplementation(kotlin("test"))
    testImplementation("org.mockito:mockito-core:4.5.1")
    testImplementation("org.mockito.kotlin:mockito-kotlin:4.1.0")
    testImplementation("junit:junit:4.13.2")
}
