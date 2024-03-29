import java.io.FileInputStream
import java.io.InputStreamReader
import java.util.Properties
import net.thebugmc.gradle.sonatypepublisher.PublishingType.AUTOMATIC

plugins {
    id("com.android.library")
    id("net.thebugmc.gradle.sonatype-central-portal-publisher") version "1.2.3"
}

val versionName = "4.0.6"

android {
    namespace = "skin.support.constraint"
    compileSdk = 34

    defaultConfig {
        minSdk = 14
    }
    buildTypes {
        release {
            isMinifyEnabled  = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"),"proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility =  JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("io.github.liyuhaolol:skin-support:4.1.2")
    implementation("io.github.liyuhaolol:skin-support-appcompat:4.1.2")
}

var signingKeyId = ""//签名的密钥后8位
var signingPassword = ""//签名设置的密码
var secretKeyRingFile = ""//生成的secring.gpg文件目录
var ossrhUsername = ""//sonatype用户名
var ossrhPassword = "" //sonatype密码

val localProperties = project.rootProject.file("local.properties")

if (localProperties.exists()) {
    println("Found secret props file, loading props")
    val properties = Properties()

    InputStreamReader(FileInputStream(localProperties), Charsets.UTF_8).use { reader ->
        properties.load(reader)
    }
    signingKeyId = properties.getProperty("signingKeyId")
    signingPassword = properties.getProperty("signingPassword")
    secretKeyRingFile = properties.getProperty("secretKeyRingFile")
    ossrhUsername = properties.getProperty("ossrhUsername")
    ossrhPassword = properties.getProperty("ossrhPassword")

} else {
    println("No props file, loading env vars")
}


centralPortal {
    username = ossrhUsername
    password = ossrhPassword
    name = "skin-support-constraint-layout"
    group = "io.github.liyuhaolol"
    version = versionName
    pom {
        //packaging = "aar"
        name = "skin-support-constraint-layout"
        description = "Android ChangeSkin"
        url = "https://github.com/liyuhaolol/skin-support-constraint-layout"
        licenses {
            license {
                name = "The MIT License"
                url = "https://github.com/liyuhaolol/skin-support-constraint-layout/blob/master/LICENSE"
            }
        }
        developers {
            developer {
                id = "liyuhao"
                name = "liyuhao"
                email = "liyuhaoid@sina.com"
            }
        }
        scm {
            connection = "scm:git@github.com/liyuhaolol/skin-support-constraint-layout.git"
            developerConnection = "scm:git@github.com/liyuhaolol/skin-support-constraint-layout.git"
            url = "https://github.com/liyuhaolol/skin-support-constraint-layout"
        }

    }
    publishingType = AUTOMATIC
    javadocJarTask = tasks.create<Jar>("javadocEmptyJar") {
        archiveClassifier = "javadoc"
    }

}


gradle.taskGraph.whenReady {
    if (allTasks.any { it is Sign }) {
        allprojects {
            extra["signing.keyId"] = signingKeyId
            extra["signing.secretKeyRingFile"] = secretKeyRingFile
            extra["signing.password"] = signingPassword
        }
    }
}

signing {
    sign(publishing.publications)
}
