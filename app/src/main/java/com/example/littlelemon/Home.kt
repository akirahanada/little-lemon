package com.example.littlelemon

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.littlelemon.ui.theme.LittleLemonTheme

// Helper function to map resource names to drawable IDs
fun getDrawableResourceId(resourceName: String): Int? {
    return when (resourceName) {
        "greek_salad" -> R.drawable.greek_salad
        "lemon_dessert" -> R.drawable.lemon_dessert
        "grilled_fish" -> R.drawable.grilled_fish
        "pasta" -> R.drawable.pasta
        "bruschetta" -> R.drawable.bruschetta
        else -> null
    }
}

@Composable
fun Home(navController: NavHostController) {
    val context = LocalContext.current
    val database = AppDatabase.getDatabase(context)
    val menuItems by database.menuItemDao().getAllMenuItems().observeAsState(emptyList())
    
    // Search and filter state
    var searchPhrase by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("") }
    
    // Debug logging
    LaunchedEffect(menuItems) {
        println("LittleLemon: Home screen observed ${menuItems.size} menu items")
    }
    
    // Filter menu items based on search and category
    val filteredMenuItems = remember(menuItems, searchPhrase, selectedCategory) {
        var filtered = menuItems
        
        // Filter by search phrase
        if (searchPhrase.isNotBlank()) {
            filtered = filtered.filter { 
                it.title.contains(searchPhrase, ignoreCase = true) ||
                it.description.contains(searchPhrase, ignoreCase = true)
            }
        }
        
        // Filter by category
        if (selectedCategory.isNotBlank()) {
            filtered = filtered.filter { it.category.equals(selectedCategory, ignoreCase = true) }
        }
        
        filtered
    }
    
    // Get unique categories for filter buttons
    val categories = remember(menuItems) {
        menuItems.map { it.category }.distinct().sorted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Add top margin to move header down from status bar
        Spacer(modifier = Modifier.height(32.dp))
        
        // Header with logo and profile button - Fixed at top
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Logo (centered when no profile, left-aligned when profile is present)
            Box(
                modifier = Modifier.weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo),
                    contentDescription = "Little Lemon Logo",
                    modifier = Modifier
                        .height(40.dp)
                        .fillMaxWidth(0.6f),
                    contentScale = ContentScale.Fit
                )
            }
            
            // Profile button using profile.png image
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clickable {
                        navController.navigate(Profile.route)
                    }
                    .background(
                        color = Color(0xFFF4CE14), 
                        shape = CircleShape
                    )
                    .padding(2.dp),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }
        
        // Hero Section - Fixed at top
        HeroSection(
            searchPhrase = searchPhrase,
            onSearchPhraseChange = { searchPhrase = it }
        )
        
        // Menu Items - Scrollable section
        MenuItems(
            menuItems = filteredMenuItems,
            categories = categories,
            selectedCategory = selectedCategory,
            onCategorySelected = { selectedCategory = it }
        )
    }
}

@Composable
fun HeroSection(
    searchPhrase: String,
    onSearchPhraseChange: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF495E57))
            .padding(16.dp)
    ) {
        Column {
            Text(
                text = "Little Lemon",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFF4CE14)
            )
            Text(
                text = "Chicago",
                fontSize = 18.sp,
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "We are a family-owned Mediterranean restaurant, focused on traditional recipes served with a modern twist.",
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 16.dp)
                )
                
                Image(
                    painter = painterResource(id = R.drawable.hero_image),
                    contentDescription = "Hero Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Search TextField
            TextField(
                value = searchPhrase,
                onValueChange = onSearchPhraseChange,
                placeholder = { Text("Enter search phrase") },
                leadingIcon = { 
                    Icon(
                        imageVector = Icons.Default.Search, 
                        contentDescription = "Search"
                    ) 
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun MenuItems(
    menuItems: List<MenuItemRoom>,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = "ORDER FOR DELIVERY!",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp)
        )
        
        // Category filter buttons
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "All" button
            item {
                FilterButton(
                    text = "All",
                    isSelected = selectedCategory.isEmpty(),
                    onClick = { onCategorySelected("") }
                )
            }
            
            // Category buttons
            items(categories) { category ->
                FilterButton(
                    text = category.replaceFirstChar { it.uppercase() },
                    isSelected = selectedCategory.equals(category, ignoreCase = true),
                    onClick = { onCategorySelected(category) }
                )
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(menuItems) { menuItem ->
                MenuItem(menuItem = menuItem)
            }
        }
    }
}

@Composable
fun FilterButton(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF495E57) else Color(0xFFF4CE14),
            contentColor = if (isSelected) Color.White else Color.Black
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.height(40.dp)
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MenuItem(menuItem: MenuItemRoom) {
    // Debug logging for image URLs
    LaunchedEffect(menuItem.image) {
        println("LittleLemon: Loading image for ${menuItem.title}: ${menuItem.image}")
    }
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = menuItem.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = menuItem.description,
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(
                    text = "$${menuItem.price}",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF495E57)
                )
            }
            
            // Load image - check if it's a local resource or URL
            if (menuItem.image.isNotEmpty()) {
                if (menuItem.image.startsWith("http")) {
                    // External URL - use Glide
                    GlideImage(
                        model = menuItem.image,
                        contentDescription = menuItem.title,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // Local resource - use painterResource
                    val drawableId = getDrawableResourceId(menuItem.image)
                    if (drawableId != null) {
                        Image(
                            painter = painterResource(id = drawableId),
                            contentDescription = menuItem.title,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Fallback if resource not found
                        Image(
                            painter = painterResource(id = R.drawable.hero_image),
                            contentDescription = menuItem.title,
                            modifier = Modifier
                                .size(80.dp)
                                .clip(RoundedCornerShape(8.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }
                }
            } else {
                // Fallback to a placeholder image when URL is empty
                Image(
                    painter = painterResource(id = R.drawable.hero_image),
                    contentDescription = menuItem.title,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    LittleLemonTheme {
        Home(navController = rememberNavController())
    }
}
