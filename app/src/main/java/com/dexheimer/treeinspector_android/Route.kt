package com.dexheimer.treeinspector_android

import com.google.firebase.firestore.DocumentId

// Classe modelo para os dados do Firestore
data class Route(
	// Anotação para pegar o ID do documento automaticamente
	@DocumentId
	val id: String = "",

	// O nome do campo deve ser igual ao do Firestore ("nome")
	val nome: String = ""
)