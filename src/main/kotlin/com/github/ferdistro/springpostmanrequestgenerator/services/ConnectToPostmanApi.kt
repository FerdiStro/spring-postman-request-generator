package com.github.ferdistro.springpostmanrequestgenerator.services

import com.github.ferdistro.springpostmanrequestgenerator.settings.RequestGeneratorSettings
import com.intellij.codeInsight.navigation.LOG
import kotlinx.io.IOException
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ConnectToPostmanApi(private val permanentCache: PermanentCache) {


    val POSTMAN_API_URL = "https://api.getpostman.com/collections"


    fun postCollection(): Boolean {
        val apiToken = RequestGeneratorSettings.loadApiToken() ?: ""

        val loadCollectionJson = permanentCache.loadCollectionJson()


        if (apiToken.isEmpty() || loadCollectionJson == null) return false

        val url = URL(POSTMAN_API_URL)
        val connection = url.openConnection() as HttpURLConnection

        return try {
            connection.requestMethod = "POST"
            connection.setRequestProperty("X-Api-Key", apiToken)
            connection.setRequestProperty("Content-Type", "application/json")
            connection.doOutput = true

            OutputStreamWriter(connection.outputStream, Charsets.UTF_8).use { writer ->
                writer.write(loadCollectionJson)
            }

            val responseCode = connection.responseCode
            val responseBody = try {
                connection.inputStream.bufferedReader().use { it.readText() }
            } catch (e: IOException) {
                connection.errorStream?.bufferedReader()?.use { it.readText() } ?: ""
            }

            LOG.info("Response ($responseCode): $responseBody")
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