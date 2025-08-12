package com.dexheimer.treeinspector_android

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException

class MainActivity : AppCompatActivity() {

	// Declara o cliente de login do Google.
	private lateinit var googleSignInClient: GoogleSignInClient

	// Registra o callback para o resultado da tela de login.
	private val signInLauncher = registerForActivityResult(
		ActivityResultContracts.StartActivityForResult()
	) { result ->
		val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
		try {
			val account = task.getResult(ApiException::class.java)!!
			handleSignInSuccess(account)
		} catch (e: ApiException) {
			Log.w("LOGIN_FALHOU", "Google sign in failed: ${e.statusCode}")
			Toast.makeText(this, "Falha no login com Google", Toast.LENGTH_SHORT).show()
		}
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_main)

		// Configura as opções de login do Google.
		val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestEmail()
			.build()

		googleSignInClient = GoogleSignIn.getClient(this, gso)

		// Configura o botão de login.
		val signInButton: SignInButton = findViewById(R.id.sign_in_button)
		signInButton.setOnClickListener {
			startSignIn()
		}
	}

	override fun onStart() {
		super.onStart()
		// Verifica se o usuário já está logado ao iniciar o app.
		val account = GoogleSignIn.getLastSignedInAccount(this)
		if (account != null) {
			Toast.makeText(this, "Login já efetuado!", Toast.LENGTH_SHORT).show()
			handleSignInSuccess(account)
		}
	}

	// Função que inicia o processo de login.
	private fun startSignIn() {
		val signInIntent: Intent = googleSignInClient.signInIntent
		signInLauncher.launch(signInIntent)
	}

	private fun handleSignInSuccess(account: GoogleSignInAccount) {
		Log.d("LOGIN_SUCESSO", "Nome: ${account.displayName}, Email: ${account.email}, ID: ${account.id}")
		Toast.makeText(this, "Login bem-sucedido: ${account.displayName}", Toast.LENGTH_LONG).show()

		// --- CORREÇÃO AQUI ---
		// Cria a "intenção" de ir da tela atual (this) para a DashboardActivity
		val intent = Intent(this, DashboardActivity::class.java)
		// Executa a navegação
		startActivity(intent)
		// Fecha a tela de login para o usuário não conseguir voltar para ela
		finish()
	}
}