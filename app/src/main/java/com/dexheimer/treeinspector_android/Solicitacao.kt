package com.dexheimer.treeinspector_android

// Representa um documento da coleção "solicitacoes"
data class Solicitacao(
	// O Firebase vai ler o campo "id" de dentro do documento.
	val id: String = "",
	// Mapeia o campo "endereco" para a nossa classe Endereco
	val endereco: Endereco = Endereco()
)