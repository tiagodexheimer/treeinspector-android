package com.dexheimer.treeinspector_android

// Representa o objeto "endereco" dentro de uma solicitação
data class Endereco(
	val rua: String = "",
	val numero: String = "",
	val bairro: String = "",
	val cidade: String = ""
)