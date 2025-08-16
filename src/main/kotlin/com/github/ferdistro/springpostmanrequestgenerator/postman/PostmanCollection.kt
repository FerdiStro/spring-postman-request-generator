package com.github.ferdistro.springpostmanrequestgenerator.postman

data class PostmanCollection(
    var info: Info?,
    var item: List<Item>?
)

data class Info(
    val name: String,
    val schema: String
)

data class Item(
    var name: String,
    val request: Request,
    val response: List<Any>? = null
)

data class Request(
    var method: String,
    val header: List<Any>,
    var url: URL,
)

data class URL(
    val raw: String,
    val host: List<String>,
    val path: List<String>,
    var query: List<QueryItem>
)

data class QueryItem(
    val key: String, val value: String
)
