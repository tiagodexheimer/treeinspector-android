plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.android)
	// Adicione esta linha para APLICAR o plugin no módulo 'app'
	id("com.google.gms.google-services")
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

	implementation("com.google.android.gms:play-services-auth:20.7.0")

	// Firebase Bill of Materials (BoM) para gerenciar versões
	implementation(platform("com.google.firebase:firebase-bom:33.1.0"))

	// Dependência do Cloud Firestore
	implementation("com.google.firebase:firebase-firestore-ktx")
}