package com.dexheimer.treeinspector_android

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DashboardActivity : AppCompatActivity() {

	// Inicializa o Firestore
	private val db = Firebase.firestore
	private lateinit var recyclerView: RecyclerView
	private lateinit var routeList: ArrayList<Route>
	private lateinit var adapter: RouteAdapter

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_dashboard)

		recyclerView = findViewById(R.id.recyclerViewRoutes)
		recyclerView.layoutManager = LinearLayoutManager(this)

		routeList = arrayListOf()
		adapter = RouteAdapter(routeList)
		recyclerView.adapter = adapter

		fetchRoutesFromFirestore()
	}

	private fun fetchRoutesFromFirestore() {
		// Acessa a coleção "rotas" no Firestore
		db.collection("rotas")
			.get() // Pede para buscar todos os documentos da coleção
			.addOnSuccessListener { documents ->
				routeList.clear() // Limpa a lista antes de adicionar novos itens
				// Itera sobre cada documento retornado
				for (document in documents) {
					// Converte o documento em um objeto da nossa classe Route
					val route = document.toObject(Route::class.java)
					routeList.add(route)
					Log.d("FIRESTORE_SUCCESS", "Rota lida: ${route.nome}")
				}
				// Notifica o adapter que os dados mudaram, para atualizar a lista na tela
				adapter.notifyDataSetChanged()
			}
			.addOnFailureListener { exception ->
				// Em caso de erro
				Log.w("FIRESTORE_ERROR", "Erro ao buscar documentos: ", exception)
				Toast.makeText(this, "Erro ao buscar rotas.", Toast.LENGTH_SHORT).show()
			}
	}
}