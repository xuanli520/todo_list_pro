plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.siyuan.todo_list"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.siyuan.todo_list"
        minSdk = 31
        targetSdk = 35
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Room组件
    implementation("androidx.room:room-runtime:2.5.2")
    // 使用 kapt 或 ksp 代替 annotationProcessor，根据项目配置选择
    // kapt("androidx.room:room-compiler:2.5.2")
    // ksp("androidx.room:room-compiler:2.5.2")
    annotationProcessor("androidx.room:room-compiler:2.5.2")

    // ViewModel和LiveData
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.6.1")
    implementation("androidx.lifecycle:lifecycle-livedata:2.6.1")

    // RecyclerView
    implementation("androidx.recyclerview:recyclerview:1.3.0")

    // Material Design组件
    implementation("com.google.android.material:material:1.9.0")
}
