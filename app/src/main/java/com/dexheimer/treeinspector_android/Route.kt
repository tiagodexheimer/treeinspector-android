package com.dexheimer.treeinspector_android

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.Exclude

data class Route(
	@DocumentId
	val id: String = "",

	val nome: String = "",

	// Adicionamos este campo para ler a lista otimizada do Firestore
	val ordemOtimizada: List<String> = emptyList(),

	// Mantemos o campo antigo por segurança, mas não o usaremos na lógica principal
	val solicitacoesIds: List<String> = emptyList(),

	@get:Exclude
	var solicitacoes: MutableList<Solicitacao> = mutableListOf()
)