package com.dexheimer.treeinspector_android

import android.content.Intent
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

	// Inicialização da instância do Firestore
	private val db = Firebase.firestore
	private lateinit var recyclerView: RecyclerView
	private lateinit var adapter: RouteAdapter
	private var routeList = mutableListOf<Route>()

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_dashboard)

		recyclerView = findViewById(R.id.recyclerViewRoutes)
		recyclerView.layoutManager = LinearLayoutManager(this)

		// Inicializa o adapter com a lógica de clique
		adapter = RouteAdapter(routeList) { selectedRoute ->
			// Verifica se a rota tem solicitações antes de abrir o mapa
			if (selectedRoute.solicitacoes.isEmpty()) {
				Toast.makeText(this, "Esta rota não possui paradas.", Toast.LENGTH_SHORT).show()
				return@RouteAdapter
			}

			// Inicia a MapActivity, passando os dados da rota
			val intent = Intent(this, MapActivity::class.java).apply {
				val addressesAsStrings = selectedRoute.solicitacoes.map {
					"${it.endereco.lat},${it.endereco.lng},${it.endereco.rua}, ${it.endereco.numero}"
				}
				putStringArrayListExtra("ROUTE_ADDRESSES", ArrayList(addressesAsStrings))
			}
			startActivity(intent)
		}

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

				val routes = routeDocuments.toObjects(Route::class.java)
				val allSolicitacaoTasks = mutableListOf<com.google.android.gms.tasks.Task<*>>()

				for (route in routes) {
					// Usa a lista 'ordemOtimizada' como prioridade
					val idsToFetch = if (route.ordemOtimizada.isNotEmpty()) {
						route.ordemOtimizada
					} else {
						route.solicitacoesIds
					}

					if (idsToFetch.isNotEmpty()) {
						val solicitacoesTask = db.collection("solicitacoes")
							.whereIn("__name__", idsToFetch)
							.get()
							.addOnSuccessListener { solicitacaoDocuments ->
								// Lógica para reordenar as solicitações
								val solicitacoesById = solicitacaoDocuments.toObjects(Solicitacao::class.java)
									.associateBy { it.id }
								val orderedSolicitacoes = idsToFetch.mapNotNull { id -> solicitacoesById[id] }
								route.solicitacoes = orderedSolicitacoes.toMutableList()
							}
						allSolicitacaoTasks.add(solicitacoesTask)
					}
				}

				// Espera todas as buscas terminarem antes de atualizar a UI
				Tasks.whenAll(allSolicitacaoTasks).addOnCompleteListener {
					routeList.clear()
					routeList.addAll(routes)
					adapter.notifyDataSetChanged()
					Log.d("FIRESTORE", "Todas as rotas e solicitações foram carregadas na ordem correta.")
				}
			}
			.addOnFailureListener { exception ->
				Log.w("FIRESTORE_ERROR", "Erro ao buscar rotas: ", exception)
				Toast.makeText(this, "Erro ao buscar rotas.", Toast.LENGTH_SHORT).show()
			}
	}
}