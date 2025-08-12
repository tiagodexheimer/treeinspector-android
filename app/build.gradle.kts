plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
}

android {
	namespace = "com.dexheimer.treeinspector_android"
	compileSdk = 36

	defaultConfig {
		applicationId = "com.dexheimer.treeinspector_android"
		minSdk = 24
		targetSdk = 36
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
	kotlinOptions {
		jvmTarget = "11"
	}
}

dependencies {

	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.appcompat)
	implementation(libs.material)
	implementation(libs.androidx.activity)
	implementation(libs.androidx.constraintlayout)
	testImplementation(libs.junit)
	androidTestImplementation(libs.androidx.junit)
	androidTestImplementation(libs.androidx.espresso.core)

	implementation("androidx.core:core-ktx:1.9.0")
	implementation("androidx.appcompat:appcompat:1.6.1")
	implementation("com.google.android.material:material:1.10.0")
	implementation("androidx.constraintlayout:constraintlayout:2.1.4")
// ... outras dependências
// Adicione esta linha para o Google Sign-In
	implementation("com.google.android.gms:play-services-auth:20.7.0")
	// Firebase Bill of Materials (BoM) para gerenciar versões
	implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

	implementation("com.google.firebase:firebase-firestore-ktx")
//
}