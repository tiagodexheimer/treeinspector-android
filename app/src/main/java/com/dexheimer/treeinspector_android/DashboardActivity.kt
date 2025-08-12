package com.dexheimer.treeinspector_android

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class DashboardActivity : AppCompatActivity() {

	private val db = Firebase.firestore
	private lateinit var recyclerView: RecyclerView
	private lateinit var adapter: RouteAdapter
	private var routeList = mutableListOf<Route>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_dashboard)

		recyclerView = findViewById(R.id.recyclerViewRoutes)
		recyclerView.layoutManager = LinearLayoutManager(this)

		adapter = RouteAdapter(routeList)
		recyclerView.adapter = adapter

		fetchRoutesAndSolicitacoes()
	}

	private fun fetchRoutesAndSolicitacoes() {
		db.collection("rotas").get()
			.addOnSuccessListener { routeDocuments ->
				if (routeDocuments.isEmpty) {
					Toast.makeText(this, "Nenhuma rota encontrada.", Toast.LENGTH_SHORT).show()
					return@addOnSuccessListener
				}

				// Transforma os documentos de rotas em objetos
				val routes = routeDocuments.toObjects(Route::class.java)
				val allSolicitacaoTasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

				for (route in routes) {
					// Se a rota tem IDs de solicitação, busca os detalhes
					if (route.solicitacoesIds.isNotEmpty()) {
						val solicitacoesTask = db.collection("solicitacoes")
							.whereIn("__name__", route.solicitacoesIds) // Busca todos os docs com os IDs da lista
							.get()
							.addOnSuccessListener { solicitacaoDocuments ->
								// Preenche a lista de solicitações detalhadas dentro do objeto da rota
								route.solicitacoes = solicitacaoDocuments.toObjects(Solicitacao::class.java)
							}
						allSolicitacaoTasks.add(solicitacoesTask)
					}
				}

				// Espera TODAS as buscas de solicitações terminarem
				Tasks.whenAll(allSolicitacaoTasks).addOnCompleteListener {
					routeList.clear()
					routeList.addAll(routes)
					adapter.notifyDataSetChanged() // Atualiza a tela com todos os dados
					Log.d("FIRESTORE", "Todas as rotas e solicitações foram carregadas.")
				}
			}
			.addOnFailureListener { exception ->
				Log.w("FIRESTORE_ERROR", "Erro ao buscar rotas: ", exception)
				Toast.makeText(this, "Erro ao buscar rotas.", Toast.LENGTH_SHORT).show()
			}
	}
}