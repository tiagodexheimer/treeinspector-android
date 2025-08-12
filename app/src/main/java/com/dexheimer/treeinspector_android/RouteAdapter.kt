package com.dexheimer.treeinspector_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Adicionamos um listener no construtor
class RouteAdapter(
	private val routeList: List<Route>,
	private val onRouteClicked: (Route) -> Unit // Função que será chamada no clique
) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

	// ViewHolder agora também trata o clique
	inner class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		private val nameTextView: TextView = itemView.findViewById(R.id.textViewRouteName)
		private val addressesTextView: TextView = itemView.findViewById(R.id.textViewAddresses)

		fun bind(route: Route) {
			nameTextView.text = route.nome

			val addressesText = route.solicitacoes.joinToString(separator = "\n") { solicitacao ->
				val end = solicitacao.endereco
				"- ${end.rua}, ${end.numero}" // Deixando mais curto para a lista
			}
			addressesTextView.text = if (addressesText.isNotEmpty()) addressesText else "Nenhum endereço carregado."

			// Configura o clique no item inteiro
			itemView.setOnClickListener {
				onRouteClicked(route)
			}
		}
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.route_item_layout, parent, false)
		// Escondemos o TextView do status que não estamos usando
		view.findViewById<TextView>(R.id.textViewRouteStatus).visibility = View.GONE
		return RouteViewHolder(view)
	}

	override fun getItemCount(): Int {
		return routeList.size
	}

	override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
		holder.bind(routeList[position])
	}
}