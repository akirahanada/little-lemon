package com.example.littlelemon

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import java.net.URLDecoder

@Serializable
data class MenuNetwork(
    @SerialName("menu")
    val menu: List<MenuItemNetwork>
)

@Serializable
data class MenuItemNetwork(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("description")
    val description: String,
    @SerialName("price")
    val price: String,
    @SerialName("image")
    val image: String,
    @SerialName("category")
    val category: String
)

class MenuRepository {
    private val httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json()
        }
    }

    private fun mapToLocalImage(title: String, originalUrl: String): String {
        return when (title.lowercase()) {
            "greek salad" -> "greek_salad"
            "lemon desert", "lemon dessert" -> "lemon_dessert"
            "grilled fish" -> "grilled_fish"
            "pasta" -> "pasta"
            "bruschetta" -> "bruschetta"
            else -> {
                println("LittleLemon: Unknown menu item '$title', using original URL: $originalUrl")
                originalUrl
            }
        }
    }

    suspend fun fetchMenu(): List<MenuItemNetwork> {
        return try {
            println("LittleLemon: Making API request to menu URL...")
            val response = httpClient
                .get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/menu.json")
            
            // Get the raw response text first
            val responseText: String = response.body()
            println("LittleLemon: Raw API response length: ${responseText.length}")
            println("LittleLemon: Raw API response (first 500 chars): ${responseText.take(500)}")
            
            // Try to parse it manually using Json.decodeFromString
            val json = Json { ignoreUnknownKeys = true }
            val menuNetwork: MenuNetwork = json.decodeFromString(responseText)
            
            // Clean up image URLs and map to local resources
            val cleanedMenuItems = menuNetwork.menu.map { item ->
                val localImageName = mapToLocalImage(item.title, item.image)
                println("LittleLemon: Menu item '${item.title}' mapped to local image: $localImageName")
                item.copy(image = localImageName)
            }
            
            println("LittleLemon: Successfully parsed JSON, menu size: ${cleanedMenuItems.size}")
            if (cleanedMenuItems.isNotEmpty()) {
                println("LittleLemon: First menu item: ${cleanedMenuItems.first()}")
            }
            cleanedMenuItems
        } catch (e: Exception) {
            println("LittleLemon: API request failed with exception: ${e.message}")
            // Let's also try to get the raw response to see what we're getting
            try {
                val response = httpClient.get("https://raw.githubusercontent.com/Meta-Mobile-Developer-PC/Working-With-Data-API/main/menu.json")
                val responseText: String = response.body()
                println("LittleLemon: Raw response text (first 1000 chars): ${responseText.take(1000)}")
            } catch (rawException: Exception) {
                println("LittleLemon: Could not get raw response: ${rawException.message}")
            }
            e.printStackTrace()
            emptyList()
        }
    }
}
