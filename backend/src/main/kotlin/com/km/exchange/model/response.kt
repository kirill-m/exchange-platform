package com.km.exchange.model

interface RpcData

data class IndexResponse(val sales: List<String>) : RpcData