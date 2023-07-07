package com.example.snippetmanager.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.configurationprocessor.json.JSONArray
import org.springframework.stereotype.Service
import java.net.HttpURLConnection
import java.net.URL

@Value("\${SNIPPET_MANAGER_URL:http://localhost:8080/")
const val baseUrl: String = "http://localhost:8080/"


data class Authorization(
        val userId: String,
        val access: String,
        val snippetId: String
)

@Service
class AuthorizationHTTPService {
    fun createAuthorization(bearerToken: String, userId: String, authorizationType: String, snippetId: String) {
        println(baseUrl)
        val url = URL(baseUrl + "new")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Authorization", "Bearer $bearerToken")
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        val jsonInputString = "{\"userId\": \"$userId\", \"type\": \"$authorizationType\", \"snippetId\": \"$snippetId\"}"

        connection.outputStream.use { os ->
            val input = jsonInputString.toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
        }

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
    }

     fun getMyAuthorizations(bearerToken: String): List<Authorization> {
        val url = URL(baseUrl + "me")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.setRequestProperty("Authorization", "Bearer $bearerToken")
        val response = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
        val jsonResponse = JSONArray(response)
        println(jsonResponse)
        val authorizations = mutableListOf<Authorization>()
        for (i in 0 until jsonResponse.length()) {
            val auth = jsonResponse.getJSONObject(i)
            authorizations.add(Authorization(auth.getString("userId"), auth.getString("access"), auth.getString("snippetId")))
        }
        return authorizations
    }

    fun isAuthorized(bearerToken: String, userId: String, authorizationType: String, snippetId: String): Boolean {
        val url = URL(baseUrl + "isAuthorized")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "POST"
        connection.doOutput = true
        connection.setRequestProperty("Authorization", "Bearer $bearerToken")
        connection.setRequestProperty("Content-Type", "application/json; utf-8")
        val jsonInputString = "{\"userId\": \"$userId\", \"type\": \"$authorizationType\", \"snippetId\": \"$snippetId\"}"

        connection.outputStream.use { os ->
            val input = jsonInputString.toByteArray(charset("utf-8"))
            os.write(input, 0, input.size)
        }

        val response = connection.inputStream.bufferedReader().use { it.readText() }
        connection.disconnect()
        return response.toBoolean()
    }
}