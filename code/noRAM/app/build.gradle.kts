plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.noram"
    compileSdk = 34

    packagingOptions.resources.excludes.add("/META-INF/*")

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
    tasks.withType<Test>{
        useJUnitPlatform()
    }
}

dependencies {

    // WARNING this is a local machine hack to fix the issue with the android.jar file not being found
    // If your project is not building, please remove this line and try to build again
    // implementation(files("/Users/christiaan/Library/Android/sdk/platforms/android-34/android.jar"))

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.8.0")
//    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
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
    
    // Add the dependencies for the Firebase Cloud Messaging
    implementation("com.google.firebase:firebase-messaging")

    // Google Auth
    implementation("com.google.auth:google-auth-library-oauth2-http:1.19.0")

    // OkHttp Library to send HTTP requests
    implementation("com.squareup.okhttp3:okhttp:4.3.1")

    // Confetti Library
    implementation("nl.dionsegijn:konfetti-xml:2.0.4")

    //Maps API
    implementation("org.osmdroid:osmdroid-android:6.1.18")
    //Location Google Play API
    implementation("com.google.android.gms:play-services-location:21.2.0")
}