plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.noram"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.noram"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

}

dependencies {

    // WARNING this is a local machine hack to fix the issue with the android.jar file not being found
    // If your project is not building, please remove this line and try to build again
    // implementation(files("/Users/christiaan/Library/Android/sdk/platforms/android-34/android.jar"))

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:5.11.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.5.1") {
        exclude(module = "protobuf-lite")
    }
    androidTestImplementation("org.mockito:mockito-android:5.11.0")

    // Firebase BOM dependency
    implementation(platform("com.google.firebase:firebase-bom:32.7.2"))
    // Firebase Firestore dependency
    implementation("com.google.firebase:firebase-firestore")
    // Firebase Auth dependency
    implementation("com.google.firebase:firebase-auth")
    // Add the dependency for the Cloud Storage library
    implementation("com.google.firebase:firebase-storage")

    // QR Scanner
    implementation("com.github.yuriy-budiyev:code-scanner:2.3.0")

    //Photos from device
    implementation("com.github.dhaval2404:imagepicker:2.1")

}