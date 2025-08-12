package com.dexheimer.treeinspector_android

// Representa um documento da coleção "solicitacoes"
data class Solicitacao(
	// Anotação removida. Agora o Firebase vai ler o campo "id" de dentro do documento.
	val id: String = "",
	val endereco: Endereco = Endereco()
)