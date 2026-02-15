package ru.diaries.mydiaries.feature.track.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

object NominatimGeocoderService {

    private const val BASE_URL = "https://nominatim.openstreetmap.org/reverse"
    private const val RATE_LIMIT_MS = 1100L

    private val mutex = Mutex()
    private var lastRequestTime = 0L

    suspend fun reverseGeocode(latitude: Double, longitude: Double): String? =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                val now = System.currentTimeMillis()
                val elapsed = now - lastRequestTime
                if (elapsed < RATE_LIMIT_MS) {
                    delay(RATE_LIMIT_MS - elapsed)
                }
                lastRequestTime = System.currentTimeMillis()
            }

            try {
                val urlString = "$BASE_URL?format=json" +
                    "&lat=$latitude" +
                    "&lon=$longitude" +
                    "&accept-language=ru" +
                    "&zoom=18" +
                    "&addressdetails=1"

                val connection = URL(urlString).openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.setRequestProperty("User-Agent", "MyDiaries-Android/1.0")
                connection.connectTimeout = 10_000
                connection.readTimeout = 10_000

                try {
                    if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                        return@withContext null
                    }

                    val reader = BufferedReader(InputStreamReader(connection.inputStream))
                    val response = reader.readText()
                    reader.close()

                    val json = JSONObject(response)
                    parseAddress(json)
                } finally {
                    connection.disconnect()
                }
            } catch (_: Exception) {
                null
            }
        }

    private fun parseAddress(json: JSONObject): String? {
        if (!json.has("address")) {
            return json.optString("display_name").ifEmpty { null }
        }

        val address = json.getJSONObject("address")

        val road = address.optString("road", "")
        val houseNumber = address.optString("house_number", "")
        val suburb = address.optString("suburb", "")
        val city = address.optString("city", "")
            .ifEmpty { address.optString("town", "") }
            .ifEmpty { address.optString("village", "") }

        val parts = mutableListOf<String>()
        if (road.isNotEmpty()) {
            val street = if (houseNumber.isNotEmpty()) "$road, $houseNumber" else road
            parts.add(street)
        }
        if (suburb.isNotEmpty()) parts.add(suburb)
        if (city.isNotEmpty()) parts.add(city)

        return parts.joinToString(", ").ifEmpty {
            json.optString("display_name").ifEmpty { null }
        }
    }
}
