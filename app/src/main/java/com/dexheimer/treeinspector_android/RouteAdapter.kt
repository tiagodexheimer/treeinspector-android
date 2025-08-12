package com.dexheimer.treeinspector_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RouteAdapter(private val routeList: List<Route>) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

	class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val nameTextView: TextView = itemView.findViewById(R.id.textViewRouteName)
		val statusTextView: TextView = itemView.findViewById(R.id.textViewRouteStatus)
		// --- ADICIONE ESTA LINHA ---
		val addressesTextView: TextView = itemView.findViewById(R.id.textViewAddresses)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.route_item_layout, parent, false)
		return RouteViewHolder(view)
	}

	override fun getItemCount(): Int {
		return routeList.size
	}

	// Dentro da classe RouteAdapter

	override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
		val route = routeList[position]
		holder.nameTextView.text = route.nome

		// --- ALTERAÇÃO AQUI ---
		// Em vez de mostrar o ID, simplesmente escondemos o campo de texto.
		holder.statusTextView.visibility = View.GONE

		// Cria a string formatada a partir da lista de objetos Solicitacao
		val addressesText = route.solicitacoes.joinToString(separator = "\n") { solicitacao ->
			val end = solicitacao.endereco
			"- ${end.rua}, ${end.numero}, ${end.bairro}, ${end.cidade}"
		}

		holder.addressesTextView.text = if (addressesText.isNotEmpty()) addressesText else "Nenhum endereço carregado."
	}
}