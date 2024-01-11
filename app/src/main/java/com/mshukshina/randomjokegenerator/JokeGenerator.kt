package com.mshukshina.randomjokegenerator

import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

class JokeGenerator {

    private val json: MediaType = "application/json".toMediaType()

    private var logging = HttpLoggingInterceptor()
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    var callBack: (result: String?) -> Unit = {}

    companion object {
        private const val API_KEY = "PLEASE ENTER YOUR API KEY HERE"
    }

    fun callAPI(question: String?) {

        logging.setLevel(HttpLoggingInterceptor.Level.BODY)

        val messageList = JSONArray()
        val message = JSONObject()
        var result: String?
        try {
            message.put("role", "system")
            message.put("content", "You are a helpful assistant.")
            message.put("role", "user")
            message.put(
                "content",
                "Make a funny joke about $question. Please don't repeat the same stuff."
            )
            messageList.put(message)
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        // Convert the list to a JSON object
        val jsonObject = JSONObject()
        try {
            jsonObject.put("messages", messageList)
            jsonObject.put("model", "gpt-3.5-turbo")
            jsonObject.put("max_tokens", 300)
            jsonObject.put("temperature", 0.8)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val body: RequestBody = jsonObject.toString().toRequestBody(json)
        val request: Request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $API_KEY")
            .post(body)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                result = null
                callBack(result)
            }

            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val innerJsonObject: JSONObject
                    try {
                        if (response.body == null) {
                            result = null
                            callBack(result)
                            return
                        }
                        innerJsonObject = JSONObject(response.body!!.string())
                        val choices = innerJsonObject.getJSONArray("choices")
                        val firstChoice = choices.getJSONObject(0)
                        val innerMessage = firstChoice.getJSONObject("message")
                        result = innerMessage.getString("content")
                        callBack(result)
                    } catch (e: JSONException) {
                        throw RuntimeException(e)
                    }
                } else {
                    result = null
                    callBack(result)
                }
            }
        })
    }

}