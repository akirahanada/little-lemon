package com.example.littlelemon

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.littlelemon.ui.theme.LittleLemonTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    
    private val menuRepository = MenuRepository()
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize database
        database = AppDatabase.getDatabase(this)
        
        // Fetch menu data from network and store in database
        fetchAndStoreMenuData()
        
        setContent {
            LittleLemonTheme {
                val navController = rememberNavController()
                MyNavigation(navController = navController)
            }
        }
    }
    
    private fun fetchAndStoreMenuData() {
        lifecycleScope.launch {
            try {
                println("LittleLemon: Starting to fetch menu data...")
                val menuItems = menuRepository.fetchMenu()
                println("LittleLemon: Fetched ${menuItems.size} menu items")
                val roomMenuItems = menuItems.map { it.toMenuItemRoom() }
                withContext(Dispatchers.IO) {
                    database.menuItemDao().insertAll(roomMenuItems)
                    println("LittleLemon: Successfully inserted ${roomMenuItems.size} items into database")
                }
            } catch (e: Exception) {
                println("LittleLemon: Error fetching menu data: ${e.message}")
                e.printStackTrace()
            }
        }
    }
}