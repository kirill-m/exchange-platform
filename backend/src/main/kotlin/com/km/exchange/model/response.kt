package com.km.exchange.model

interface RpcData

data class IndexResponse(val sales: List<String>) : RpcData
data class LoginResponse(val user: User? = null, val error: String? = null) : RpcData
data class RegisterResponse(val user: User? = null, val error: String? = null) : RpcData