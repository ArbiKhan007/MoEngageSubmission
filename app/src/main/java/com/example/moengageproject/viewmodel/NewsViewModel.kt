package com.example.moengageproject.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moengageproject.model.Article
import com.example.moengageproject.model.Response
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL

class NewsViewModel : ViewModel() {
    val newsLiveData: MutableLiveData<List<Article>> = MutableLiveData(emptyList())

    val coroutineExceptionHandler = CoroutineExceptionHandler{_, throwable ->
        throwable.printStackTrace()
    }

    fun callApi() {
        val apiUrl =
            "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json" //API endpoint
        try {
            val url: URL = URI.create(apiUrl).toURL()
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection

            //Perform Network operation on IO thread
            CoroutineScope(Dispatchers.IO+coroutineExceptionHandler).launch {
                //Request method: GET
                connection.requestMethod = "GET"

                // Response code
                val responseCode: Int = connection.responseCode
                println("Response Code: $responseCode")

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Read and print the response data
                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    var line: String?
                    val response = StringBuilder()

                    //Append new line on IO while blocking the thread
                    withContext(Dispatchers.IO) {
                        while (reader.readLine().also { line = it } != null) {
                            response.append(line)
                        }

                        reader.close()
                    }

                    //Json String to JSON Object
                    val jsonResponse = Gson().fromJson(response.toString(), Response::class.java)
                    newsLiveData.postValue(jsonResponse.articles)
                } else {
                    println("Error: Unable to fetch data from the API")
                }

                // Close the connection
                connection.disconnect()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}