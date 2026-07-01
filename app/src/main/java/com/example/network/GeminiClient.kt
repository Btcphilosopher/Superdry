package com.example.network

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiClient {
    private const val TAG = "GeminiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    suspend fun askAssistant(prompt: String, systemInstruction: String, history: List<Pair<String, Boolean>> = emptyList()): String = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "API Key is missing or using placeholder")
            return@withContext getOfflineResponse(prompt)
        }

        val url = "https://generativelanguage.googleapis.com/v1beta/models/$MODEL_NAME:generateContent?key=$apiKey"
        
        try {
            // Build the JSON request body
            val root = JSONObject()
            
            // Contents array
            val contentsArray = JSONArray()
            
            // Add history
            for (turn in history) {
                val contentObj = JSONObject()
                contentObj.put("role", if (turn.second) "user" else "model")
                val partsArr = JSONArray()
                val partObj = JSONObject()
                partObj.put("text", turn.first)
                partsArr.put(partObj)
                contentObj.put("parts", partsArr)
                contentsArray.put(contentObj)
            }
            
            // Add current user prompt
            val currentUserContent = JSONObject()
            currentUserContent.put("role", "user")
            val currentPartsArr = JSONArray()
            val currentPartObj = JSONObject()
            currentPartObj.put("text", prompt)
            currentPartsArr.put(currentPartObj)
            currentUserContent.put("parts", currentPartsArr)
            contentsArray.put(currentUserContent)
            
            root.put("contents", contentsArray)

            // System instruction
            if (systemInstruction.isNotEmpty()) {
                val systemInstructionObj = JSONObject()
                val systemPartsArr = JSONArray()
                val systemPartObj = JSONObject()
                systemPartObj.put("text", systemInstruction)
                systemPartsArr.put(systemPartObj)
                systemInstructionObj.put("parts", systemPartsArr)
                root.put("systemInstruction", systemInstructionObj)
            }

            // Generation config
            val generationConfig = JSONObject()
            generationConfig.put("temperature", 0.7)
            generationConfig.put("maxOutputTokens", 1000)
            root.put("generationConfig", generationConfig)

            val requestBody = root.toString().toRequestBody("application/json; charset=utf-8".toMediaType())
            
            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            client.newCall(request).execute().use { response ->
                val bodyString = response.body?.string()
                if (response.isSuccessful && bodyString != null) {
                    val responseJson = JSONObject(bodyString)
                    val candidates = responseJson.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        if (content != null) {
                            val parts = content.optJSONArray("parts")
                            if (parts != null && parts.length() > 0) {
                                return@withContext parts.getJSONObject(0).optString("text", "I'm sorry, I couldn't generate a recommendation.")
                            }
                        }
                    }
                }
                Log.e(TAG, "API Error: code=${response.code} body=$bodyString")
                return@withContext "Error communicating with AI Assistant. Let me help you with our local shopping tips:\n\n${getOfflineResponse(prompt)}"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during API call", e)
            return@withContext "I'm experiencing connectivity issues. Here is a simulated shopping tip based on your query:\n\n${getOfflineResponse(prompt)}"
        }
    }

    private fun getOfflineResponse(prompt: String): String {
        val lower = prompt.lowercase()
        return when {
            lower.contains("size") -> {
                "**Superdry Size Guide Recommendation**:\n\nOur jackets and slim hoodies tend to run slightly fitted in true Japanese collegiate styling. If you are between sizes, we highly recommend choosing **one size larger** for a comfortable casual fit, particularly for products in our *Ultimate Windcheater* and *Everest Parka* ranges.\n\n- **Chest Sizing**:\n  - Small: 36\" (91cm)\n  - Medium: 38\" (97cm)\n  - Large: 40\" (102cm)\n  - X-Large: 42\" (107cm)"
            }
            lower.contains("jacket") || lower.contains("windcheater") || lower.contains("parka") -> {
                "**Superdry Outerwear Specialist Suggestion**:\n\nFor premium wind protection and light rain shield, the **SD Ultimate Windcheater** (with signature triple-zip) is our absolute bestseller. For heavy winter sub-zero warmth, we highly recommend the **Everest Premium Parka**, featuring deep insulation and high fleece linings. \n\nBoth pair perfectly with our *Vintage Denim* jeans and *Sleek Court Trainers* for an iconic street style look."
            }
            lower.contains("hoodie") || lower.contains("sweat") -> {
                "**Superdry Athletic-Casual Guide**:\n\nOur hoodies are crafted with ultra-dense loopback combed cotton which offers a heavy-hand premium luxury weight. \n- The **Vintage Logo Script Hoodie** has a classic relaxed kangaroo fit.\n- The **Orange Label Zip Hoodie** features a tailored slim silhouette, ideal for layering under one of our signature leather jackets."
            }
            lower.contains("outfit") || lower.contains("match") || lower.contains("style") -> {
                "**Superdry Stylist Curated Look**:\n\nHere is a highly recommended urban minimalist combination:\n1. **Base**: Organic Cotton Core Tee in Pure White.\n2. **Layer**: SD Ultimate Windcheater in Jet Black/Orange details.\n3. **Bottom**: Slim Fit Vintage Denim in Indigo Wash.\n4. **Footwear**: Sleek Court Trainers in Bright White.\n\nThis look is clean, high-contrast, and effortlessly stylish!"
            }
            else -> {
                "Thank you for contacting Superdry's AI Assistant! \n\nI can help you find products, suggest premium outfit combinations, recommend the perfect size, or answer queries about our flagship garments. \n\nTry asking me: \n- *'What size should I get for jackets?'*\n- *'Can you recommend a stylish weekend outfit?'*\n- *'What makes the Ultimate Windcheater special?'*"
            }
        }
    }
}
