package com.dexheimer.treeinspector_android

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RouteAdapter(private val routeList: List<Route>) : RecyclerView.Adapter<RouteAdapter.RouteViewHolder>() {

	// Esta classe representa cada item visual da lista (o route_item_layout)
	class RouteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
		val nameTextView: TextView = itemView.findViewById(R.id.textViewRouteName)
		val statusTextView: TextView = itemView.findViewById(R.id.textViewRouteStatus)
	}

	// Cria a view para um novo item da lista
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
		val view = LayoutInflater.from(parent.context).inflate(R.layout.route_item_layout, parent, false)
		return RouteViewHolder(view)
	}

	// Retorna o número total de itens na lista
	override fun getItemCount(): Int {
		return routeList.size
	}

	// Conecta os dados de uma rota específica à view

	override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
		val route = routeList[position]
		// Ajuste aqui para usar a propriedade "nome"
		holder.nameTextView.text = route.nome
		// Vamos remover o status por enquanto, já que não temos esse campo
		holder.statusTextView.text = "ID: ${route.id}" // Mostrando o ID como exemplo
	}
}