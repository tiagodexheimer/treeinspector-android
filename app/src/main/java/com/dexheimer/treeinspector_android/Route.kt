package com.dexheimer.treeinspector_android

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Route(
	@DocumentId
	val id: String = "",

	val nome: String = "",

	// Mapeia a lista de IDs do Firestore
	val solicitacoesIds: List<String> = emptyList(),

	// Esta lista NÃO será lida do Firestore, nós a preencheremos com código.
	// Ela guardará os detalhes completos de cada solicitação.
	@get:Exclude
	var solicitacoes: MutableList<Solicitacao> = mutableListOf()
)