plugins {
    java
    application
    id("org.javamodularity.moduleplugin") version "1.8.12"
    id("org.openjfx.javafxplugin") version "0.0.13"
    id("org.beryx.jlink") version "3.1.3"
}

group = "com.titiplex"
version = "1.0.1"

repositories {
    mavenCentral()
}

val junitVersion = "5.10.2"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(23)
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

application {
    mainModule.set("com.titiplex.comptaapp")
    mainClass.set("com.titiplex.comptaapp.WebApp")
}

javafx {
    version = "23.0.1"
    modules = listOf("javafx.controls", "javafx.fxml", "javafx.web", "javafx.swing", "javafx.media")
}

dependencies {
    implementation("org.controlsfx:controlsfx:11.2.1")
    implementation("com.dlsc.formsfx:formsfx-core:11.6.0") {
        exclude(group = "org.openjfx")
    }
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")
    implementation("net.synedra:validatorfx:0.5.0") {
        exclude(group = "org.openjfx")
    }
    implementation("org.kordamp.ikonli:ikonli-javafx:12.3.1")
    implementation("org.kordamp.bootstrapfx:bootstrapfx-core:0.4.0")
    implementation("eu.hansolo:tilesfx:21.0.3") {
        exclude(group = "org.openjfx")
    }
    implementation("com.github.almasb:fxgl:17.3") {
        exclude(group = "org.openjfx")
        exclude(group = "org.jetbrains.kotlin")
    }
    // implementation("org.xerial:sqlite-jdbc:3.50.2.0")
    implementation("com.h2database:h2:2.3.232")
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitVersion}")

    implementation("com.github.librepdf:openpdf:1.3.32")

    implementation("org.apache.poi:poi-ooxml:5.4.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

jlink {
    imageZip.set(layout.buildDirectory.file("/distributions/app-${javafx.platform.classifier}.zip"))
    options.set(listOf("--strip-debug", "--compress=2", "--no-header-files", "--no-man-pages"))
    launcher {
        name = "ComptaApp"
    }
    /**
     * jpackage (installateur)
     * Génère un MSI auto-contenu : pas besoin de JRE installé chez l’utilisateur.
     */
    jpackage {
        // Windows : "msi" (ou "exe")
        installerType = "exe"

        // Nom affiché et nom interne de l’app-image
        imageName = "ComptaApp"

        // Version app/installeur
        // [!!] jpackage exige une version purement numérique
        // si tu veux garder project.version = "1.0-SNAPSHOT", fais plutôt :
        // appVersion = project.version.toString().replace(Regex("[^0-9.]"), "").ifBlank { "1.0.0" }
        appVersion = project.version.toString()

        // Éditeur
        vendor = "Titiplex"

        // Icône et ressources (README, LICENSE…)
        icon = file("src/installer/icon.ico").absolutePath
        resourceDir = file("src/installer")

        // Options Windows
        installerOptions = listOf(
            "--win-dir-chooser",          // l’utilisateur peut choisir le dossier d’install
            "--win-per-user-install",     // pas besoin d’admin (installe dans AppData)
            "--win-menu",                 // entrée Menu Démarrer
            "--win-menu-group", "ComptaApp",
            "--win-shortcut",             // raccourci Bureau
            "--win-shortcut-prompt",      // propose la création de raccourcis pendant l’install
            "--win-upgrade-uuid", "2bbce8f9-fabc-428e-97a2-46f0c9aa79e2"
        )

        // Description de l’app dans l’installeur
        installerOptions = installerOptions + listOf("--description", "Application de comptabilité – ComptaApp")

        // Si ton app a besoin d'autres fichiers (ex: logos, modèles PDF…),
        // place-les dans src/installer/app/ ; ils seront copiés à côté de l’exécutable.
        // Exemple : src/installer/app/images/logo.png
    }
}

/**
 * Tâche de confort pour générer directement l’installateur
 * (équivalent à `gradlew jpackage`).
 */
tasks.register("makeInstaller") {
    group = "distribution"
    description = "Build runtime image + MSI installer via jpackage"
    dependsOn("jpackage")
}
