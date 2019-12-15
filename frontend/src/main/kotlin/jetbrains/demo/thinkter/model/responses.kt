package org.jetbrains.demo.thinkter.model

import jetbrains.demo.thinkter.model.Sale
import jetbrains.demo.thinkter.model.User

interface RpcData

data class IndexResponse(val sales: List<Sale>) : RpcData
data class CreateSaleToken(val user: String, val date: Long, val code: String) : RpcData
data class CreateSaleResult(val sale: Sale) : RpcData
data class UserThoughtsResponse(val user: User, val thoughts: List<Sale>) : RpcData
data class ViewThoughtResponse(val thought: Sale, val date: Long, val code: String?) : RpcData
data class LoginResponse(val user: User? = null, val error: String? = null) : RpcData