package com.km.exchange.model

interface RpcData

data class IndexResponse(val sales: List<Sale>) : RpcData
data class PollResponse(val time: Long, val count: String) : RpcData
data class LoginResponse(val user: User? = null, val error: String? = null) : RpcData
data class RegisterResponse(val user: User? = null, val error: String? = null) : RpcData
data class CreateSaleToken(val user: String, val date: Long, val code: String) : RpcData
data class CreateSaleResponse(val sale: Sale) : RpcData