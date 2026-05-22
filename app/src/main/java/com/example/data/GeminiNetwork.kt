package com.example.data

import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(val text: String)

@JsonClass(generateAdapter = true)
data class Content(val parts: List<Part>, val role: String? = "user")

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class PartResponse(val text: String?)

@JsonClass(generateAdapter = true)
data class ContentResponse(val parts: List<PartResponse>?)

@JsonClass(generateAdapter = true)
data class Candidate(val content: ContentResponse?)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(val candidates: List<Candidate>?)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object RetrofitClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(MoshiConverterFactory.create())
        .build()

    val service: GeminiApiService = retrofit.create(GeminiApiService::class.java)
}

suspend fun askGemini(prompt: String, systemPrompt: String = "You are an expert SMC trading assistant."): String {
    return try {
        val apiKey = com.example.BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY" || apiKey.contains("placeholder", ignoreCase = true)) {
            return generateLocalTradingAdvice(prompt)
        }

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            systemInstruction = Content(parts = listOf(Part(text = systemPrompt)))
        )

        val response = RetrofitClient.service.generateContent(apiKey, request)
        response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
            ?: "Intelligence Engine did not yield a direct text response. Try adjusting your trade details!"
    } catch (e: Exception) {
        // Fallback or warning message
        "Connection Error: ${e.message}\n\n[Active Fallback Mode]:\n${generateLocalTradingAdvice(prompt)}"
    }
}

private fun generateLocalTradingAdvice(prompt: String): String {
    val lowPrompt = prompt.lowercase().trim()
    return when {
        lowPrompt.contains("hello") || lowPrompt.contains("hi") || lowPrompt.contains("hey") || lowPrompt.contains("who are you") || lowPrompt.contains("help") -> {
            "👋 **Greetings, Elite Trader!**\n\n" +
            "I am your local **SmartMoney Coach Workspace**. I am fully offline-enabled to guide you safely through challenge sessions!\n\n" +
            "Feel free to ask me about:\n" +
            "• **SMC terms** (e.g. 'what is bos', 'fvg', 'order blocks', 'liquidity')\n" +
            "• **Risk questions** (e.g. 'risk management', 'lot size', 'drawdown')\n" +
            "• **Emotions** (e.g. 'fear', 'greed', 'revenge trading', 'overtrading')\n\n" +
            "ℹ️ *Tip: To connect my live dynamic Gemini AI engine, simply click on the Secrets manager panel (🔑 icon on the platform) and define a live key for 'GEMINI_API_KEY'!*"
        }
        lowPrompt.contains("revenge") || lowPrompt.contains("lost") || lowPrompt.contains("loss") || lowPrompt.contains("lose") -> {
            "⚠️ **[SMC Risk Alert - Revenge Trading]**:\n\n" +
            "Detecting potential high-stress emotional loops. Trying to immediately win back a standard market variance loss triggers hasty entries with no clear structural sweep.\n\n" +
            "**Action Steps:**\n" +
            "1. Close your active chart tab immediately and step away from the desk.\n" +
            "2. Realize that stops are business expenses. Every setup has statistical expectancy.\n" +
            "3. Wait for the standard London or NY session overlaps before scanning for liquidations."
        }
        lowPrompt.contains("overtrade") || lowPrompt.contains("overtrading") || lowPrompt.contains("frequency") -> {
            "⚠️ **[SMC Discipline Audit - Overtrading]**:\n\n" +
            "You are trading too frequently or forcing entries during low-volume chop (Asian Session or mid-day gaps).\n\n" +
            "**Optimal Strategy:**\n" +
            "• Restrict your executions to a maximum of **2 high-quality trades per day**.\n" +
            "• Only trade in the premium session windows (London: 07:00-10:00 GMT | New York: 13:00-16:00 GMT).\n" +
            "• Remember: Safeguarding your equity capital is your primary job."
        }
        lowPrompt.contains("bos") || lowPrompt.contains("choch") || lowPrompt.contains("structure") -> {
            "🔍 **[SMC Fundamentals - Market Structure Shift]**:\n\n" +
            "• **BOS (Break of Structure):** Occurs with the trend direction. When price closes past a swing high/low, confirming strong continuation.\n" +
            "• **CHoCH (Change of Character):** The first sub-structural break signalling a shift in order flow dominance (e.g. from bearish to bullish trends).\n\n" +
            "**Execution Tip:** Never enter directly on a CHoCH. Wait for price to pull back into a premium/discount **Optimal Trade Entry (OTE)** region (typically 62% - 79% Fibonacci ratios) matching an unfilled Order Block."
        }
        lowPrompt.contains("fvg") || lowPrompt.contains("gap") || lowPrompt.contains("imbalance") -> {
            "⚡ **[SMC Structural Imbalances - Fair Value Gaps (FVGs)]**:\n\n" +
            "An FVG is a 3-candle structural anomaly where price expands rapidly in one direction, leaving behind inefficient delivery. It is measured from the Wick of Candle 1 to the Wick of Candle 3.\n\n" +
            "**How to Trade Gaps:**\n" +
            "Price naturally acts like a magnet to these inefficient voids. Always treat an unfilled bearish FVG in a premium zone as an excellent target area for short positions after a liquidity hunt."
        }
        lowPrompt.contains("order block") || lowPrompt.contains("ob") || lowPrompt.contains("block") -> {
            "🧱 **[SMC Institutional footstep - Order Blocks (OBs)]**:\n\n" +
            "An Order Block represents the last opposing candle prior to a massive high-volume expansion that breaks market structure.\n\n" +
            "**Validation Checklist:**\n" +
            "- Did the OB cause an immediate **Break of Structure (BOS)**?\n" +
            "- Is there an unfilled **Fair Value Gap** immediately above/below it?\n" +
            "- Only trade *mitigations* (the first touch back into the block). Multiple mitigation touches decrease OB strength!"
        }
        lowPrompt.contains("liquidity") || lowPrompt.contains("sweep") || lowPrompt.contains("pool") -> {
            "💧 **[SMC Core Engine - Liquidity Pools & Sweeps]**:\n\n" +
            "SMC dictates that markets move from one liquidity pool to another. Stop losses cluster at obvious Double Tops (Buy Side Liquidity / BSL) and Double Bottoms (Sell Side Liquidity / SSL).\n\n" +
            "**Elite Rule:** Retail traders enter at double tops/bottoms expecting support/resistance. SMC professional traders wait for institutions to **sweep** these stop-loss clusters before participating in the rebound."
        }
        lowPrompt.contains("risk") || lowPrompt.contains("lot") || lowPrompt.contains("size") || lowPrompt.contains("leverage") || lowPrompt.contains("drawdown") -> {
            "📉 **[SMC Risk Management Protocol]**:\n\n" +
            "To safeguard your funded prop accounts long-term:\n" +
            "1. **Maximum Stop Risk:** Never exceed **1.0% to 2.0%** of your starting balance on any single sequence.\n" +
            "2. **Drawdown Boundary:** Cap your daily loss limit at **4.0%** (step away if hit).\n" +
            "3. **Lot Size Calculation:** Use the standard formula:\n" +
            "   `Lot Size = (Starting Balance * Risk%) / (Stop Loss in Pips * Value Per Pip)`"
        }
        lowPrompt.contains("emotion") || lowPrompt.contains("fear") || lowPrompt.contains("greed") || lowPrompt.contains("psychology") || lowPrompt.contains("mindset") -> {
            "🧠 **[SMC Psychological Calibration]**:\n\n" +
            "The finest technical SMC strategy will fail without sound execution psychology:\n" +
            "• **FOMO (Fear of Missing Out):** Entering a run with no retracement. Realize that markets offer infinite cycles.\n" +
            "• **Hasty Breakeven Shifts:** Moving stop loss to entry too quickly due to fear of losing. Let the structure breathe!\n" +
            "• **Greed:** Over-leveraging to speed up profit payouts. Stick strictly to the planned 1:3 minimum risk-to-reward parameters."
        }
        else -> {
            "💡 **[SmartMoney Coach - Local SMC Feedback]**:\n\n" +
            "Evaluating input patterns... I am currently operating in **Local Offline Companion Mode**.\n\n" +
            "**Observations:**\n" +
            "- Waiting for key market triggers is always better than chasing price movements.\n" +
            "- Keep risk consistently below 1.5% to preserve capital during market consolidation phases.\n" +
            "- Type 'help', 'SMC terms', 'liquidity', 'BOS', 'order block', 'fear', or 'risk management' for detailed local training guidelines.\n\n" +
            "🚀 *To unleash unlimited dynamic AI insights, define your live \"GEMINI_API_KEY\" in Google AI Studio's Secrets manager pane!*"
        }
    }
}
