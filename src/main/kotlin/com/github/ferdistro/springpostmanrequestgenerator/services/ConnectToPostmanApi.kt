package com.github.ferdistro.springpostmanrequestgenerator.services

import com.github.ferdistro.springpostmanrequestgenerator.settings.RequestGeneratorSettings
import kotlinx.io.IOException
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import kotlin.concurrent.thread

private const val POSTMAN_API_URL = "https://api.getpostman.com/collections"
private const val API_TOKEN_HEADER = "X-Api-Key"
private const val API_CONTENT_TYPE_HEADER = "Content-Type"
private const val API_CONTENT_TYPE_VALUE = "application/json"
private const val API_GET = "GET"
private const val API_POST = "POST"
private const val API_PUT = "PUT"

class ConnectToPostmanApi(private val permanentCache: PermanentCache) {


    companion object {
        fun verifyApiToken(apiToken: String): Boolean {
            if (apiToken.isBlank()) return false
            return try {
                val url = URI.create("https://api.getpostman.com/me").toURL()
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = API_GET
                connection.setRequestProperty("X-Api-Key", apiToken)
                connection.setRequestProperty("Content-Type", "application/json")
                connection.connectTimeout = 2000
                connection.readTimeout = 2000

                val code = connection.responseCode
                connection.disconnect()
                code == 200
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }


    private fun getCollectionId(): String {
        val apiToken = RequestGeneratorSettings.loadApiToken() ?: ""
        if (apiToken.isEmpty()) return ""

        val url: URL = URI.create("https://api.getpostman.com/collections").toURL()
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = API_GET
            connection.setRequestProperty(API_TOKEN_HEADER, apiToken)
            connection.setRequestProperty(API_CONTENT_TYPE_HEADER, API_CONTENT_TYPE_VALUE)
            connection.doInput = true

            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = reader.readText()
            reader.close()

            val json = JSONObject(response)
            val collections: JSONArray = json.getJSONArray("collections")

            for (i in 0 until collections.length()) {
                val collection = collections.getJSONObject(i)
                if (collection.getString("name") == "Generated Requests") {
                    return collection.getString("id")
                }
            }

            ""
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        } finally {
            connection.disconnect()
        }
    }


    fun postCollection() {
        thread {
            val apiToken = RequestGeneratorSettings.loadApiToken() ?: ""

            val loadCollectionJson = permanentCache.loadCollectionJson()


            if (apiToken.isEmpty() || loadCollectionJson == null) return@thread

            val collectionId = getCollectionId()
            val requestMethode: String = if (collectionId.isEmpty()) API_POST else API_PUT
            val url: URL = if (collectionId.isEmpty()) URI.create(POSTMAN_API_URL)
                .toURL() else URI.create("$POSTMAN_API_URL/$collectionId").toURL()

            val connection = url.openConnection() as HttpURLConnection

            try {
                connection.requestMethod = requestMethode
                connection.setRequestProperty(API_TOKEN_HEADER, apiToken)
                connection.setRequestProperty(API_CONTENT_TYPE_HEADER, API_CONTENT_TYPE_VALUE)
                connection.doOutput = true

                OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                    writer.write(loadCollectionJson)
                }

                val responseCode = connection.responseCode
                val responseBody = try {
                    connection.inputStream.bufferedReader().use { it.readText() }
                } catch (e: IOException) {
                    e.printStackTrace()
                    connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
                }

                println("Response ($responseCode): $responseBody")
                connection.responseMessage
                responseCode in 200..299
            } catch (e: Exception) {
                e.printStackTrace()
                false
            } finally {
                connection.disconnect()
            }

        }

    }
}