package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.R
import com.example.data.model.*
import com.example.ui.navigation.Screen
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.SuperdryViewModel

// Helper for finding drawable resources
@Composable
fun getProductImagePainter(resName: String): androidx.compose.ui.graphics.painter.Painter {
    return when (resName) {
        "img_hero_banner" -> painterResource(id = R.drawable.img_hero_banner)
        "img_collection_women" -> painterResource(id = R.drawable.img_collection_women)
        "img_store_map" -> painterResource(id = R.drawable.img_store_map)
        else -> painterResource(id = R.drawable.img_collection_women) // fallback
    }
}

// ==========================================
// 1. HOME SCREEN
// ==========================================
@Composable
fun HomeScreen(viewModel: SuperdryViewModel, products: List<ProductEntity>) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Hero Promotion Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(340.dp)
                .clip(RoundedCornerShape(32.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_hero_banner),
                contentDescription = "Superdry Winter Collection Hero",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            // Overlay gradient for elite high-contrast feel
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f)),
                            startY = 100f
                        )
                    )
            )

            // Vertical Japanese Text Watermark (Artistic Signature)
            Column(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 24.dp, end = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                "極度乾燥(しなさい)".forEach { char ->
                    Text(
                        text = char.toString(),
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 12.sp
                    )
                }
            }

            // Hero Brand Text
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text(
                        text = "NEW SEASON",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = "THE ART OF\nOUTERWEAR",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp,
                    lineHeight = 36.sp
                )
                Text(
                    text = "Premium engineered layers and rugged parkas.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 6.dp, bottom = 14.dp)
                )
                Button(
                    onClick = {
                        viewModel.selectedCategoryFilter.value = null
                        viewModel.navigateTo(Screen.Shop)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
                    shape = RoundedCornerShape(24.dp), // pill-shaped modern button
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp)
                ) {
                    Text("SHOP THE COLLECTION", fontWeight = FontWeight.Bold, fontSize = 11.sp, letterSpacing = 1.sp)
                }
            }
        }

        // Active Promo Strip
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Discount,
                    contentDescription = "Promo Code",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "EXCLUSIVE ONLINE OFFER",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Use code SUPERDRY20 at checkout for 20% off all apparel.",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                TextButton(
                    onClick = {
                        viewModel.promoCode.value = "SUPERDRY20"
                        Toast.makeText(context, "Code copied! Apply in shopping basket.", Toast.LENGTH_SHORT).show()
                    }
                ) {
                    Text("COPY CODE", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // Featured Collections
        Text(
            text = "FEATURED COLLECTIONS",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 12.dp)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CategoryCollectionCard("Jackets", R.drawable.img_hero_banner) {
                viewModel.selectedCategoryFilter.value = "Jackets"
                viewModel.navigateTo(Screen.Shop)
            }
            CategoryCollectionCard("Hoodies", R.drawable.img_collection_women) {
                viewModel.selectedCategoryFilter.value = "Hoodies"
                viewModel.navigateTo(Screen.Shop)
            }
            CategoryCollectionCard("Accessories", R.drawable.img_collection_women) {
                viewModel.selectedCategoryFilter.value = "Accessories"
                viewModel.navigateTo(Screen.Shop)
            }
        }

        // Horizontal Product List (New Arrivals)
        Text(
            text = "NEW ARRIVALS",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp)
        )

        val newArrivals = products.filter { it.isNewArrival }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(newArrivals) { product ->
                ProductCompactCard(product) {
                    viewModel.navigateTo(Screen.ProductDetail(product.id))
                }
            }
        }

        // Horizontal Product List (Trending / Recommended)
        Text(
            text = "RECOMMENDED FOR YOU",
            fontWeight = FontWeight.Black,
            fontSize = 18.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 12.dp)
        )

        val recommended = products.filter { !it.isNewArrival && it.id != "sd-jacket-01" }
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().padding(bottom = 32.dp)
        ) {
            items(recommended) { product ->
                ProductCompactCard(product) {
                    viewModel.navigateTo(Screen.ProductDetail(product.id))
                }
            }
        }
    }
}

@Composable
fun CategoryCollectionCard(name: String, imageRes: Int, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(130.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(id = imageRes),
                contentDescription = name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.45f))
            )
            Text(
                text = name.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                letterSpacing = 2.sp,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

@Composable
fun ProductCompactCard(product: ProductEntity, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(160.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            ) {
                Image(
                    painter = getProductImagePainter(product.imageResName),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (product.isSale) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.padding(10.dp)
                    ) {
                        Text(
                            text = "-${product.discountPercent}%",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                }
            }
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = product.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = product.category.uppercase(),
                    fontSize = 10.sp,
                    color = SoftGrey,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "$${String.format("%.2f", product.price)}",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Rating",
                        tint = LuxuryGold,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = product.averageRating.toString(),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 2.dp)
                    )
                }
            }
        }
    }
}

// ==========================================
// 2. SHOP SCREEN
// ==========================================
@Composable
fun ShopScreen(viewModel: SuperdryViewModel, products: List<ProductEntity>, categories: List<CategoryEntity>) {
    var isFilterExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Search bar & Filter trigger
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = viewModel.searchQuery.value,
                onValueChange = { viewModel.searchQuery.value = it },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp)
                    .testTag("search_input"),
                placeholder = { Text("Search Superdry Products...") },
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (viewModel.searchQuery.value.isNotEmpty()) {
                        IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                            Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(4.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = BorderGrey
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            FilledIconButton(
                onClick = { isFilterExpanded = !isFilterExpanded },
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(4.dp),
                colors = IconButtonDefaults.filledIconButtonColors(
                    containerColor = if (isFilterExpanded) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = "Filters",
                    tint = if (isFilterExpanded) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Expanding Filter & Sort Panel
        AnimatedVisibility(
            visible = isFilterExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(4.dp),
                border = BorderStroke(1.dp, BorderGrey)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("SORT & FILTER PRODUCTS", fontWeight = FontWeight.Black, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    // Sorting Row
                    Text("Sort By", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SoftGrey)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Default", "Price: Low to High", "Price: High to Low", "Top Rated").forEach { opt ->
                            val isSelected = viewModel.sortOption.value == opt
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.sortOption.value = opt },
                                label = { Text(opt) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Size selection row
                    Text("Filter Size", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SoftGrey)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("All", "S", "M", "L", "XL", "XXL").forEach { sz ->
                            val isSelected = (viewModel.selectedSizeFilter.value == sz) || (sz == "All" && viewModel.selectedSizeFilter.value == null)
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.selectedSizeFilter.value = if (sz == "All") null else sz },
                                label = { Text(sz) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Price Slider
                    Text(
                        text = "Price Range ($${viewModel.minPriceFilter.value.toInt()} - $${viewModel.maxPriceFilter.value.toInt()})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = SoftGrey
                    )
                    RangeSlider(
                        value = viewModel.minPriceFilter.value..viewModel.maxPriceFilter.value,
                        onValueChange = { range ->
                            viewModel.minPriceFilter.value = range.start
                            viewModel.maxPriceFilter.value = range.endInclusive
                        },
                        valueRange = 0f..200f,
                        colors = SliderDefaults.colors(
                            thumbColor = MaterialTheme.colorScheme.primary,
                            activeTrackColor = MaterialTheme.colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Reset button
                    Button(
                        onClick = {
                            viewModel.sortOption.value = "Default"
                            viewModel.selectedSizeFilter.value = null
                            viewModel.selectedCategoryFilter.value = null
                            viewModel.minPriceFilter.value = 0f
                            viewModel.maxPriceFilter.value = 200f
                            viewModel.searchQuery.value = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Text("RESET ALL FILTERS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Category Browsing Scroll strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val isAllSelected = viewModel.selectedCategoryFilter.value == null
            FilterChip(
                selected = isAllSelected,
                onClick = { viewModel.selectedCategoryFilter.value = null },
                label = { Text("ALL COLLECTIONS") },
                shape = RoundedCornerShape(2.dp)
            )

            categories.forEach { cat ->
                val isSelected = viewModel.selectedCategoryFilter.value == cat.name
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.selectedCategoryFilter.value = cat.name },
                    label = { Text(cat.name.uppercase()) },
                    shape = RoundedCornerShape(2.dp)
                )
            }
        }

        // Grid listing of products matching query/filters
        val filteredProducts = products.filter { product ->
            // Category filter
            val matchCategory = viewModel.selectedCategoryFilter.value == null || product.category == viewModel.selectedCategoryFilter.value
            // Search query filter
            val matchQuery = viewModel.searchQuery.value.isBlank() || product.name.lowercase().contains(viewModel.searchQuery.value.lowercase()) || product.description.lowercase().contains(viewModel.searchQuery.value.lowercase())
            // Size filter
            val matchSize = viewModel.selectedSizeFilter.value == null || product.sizesCsv.split(",").contains(viewModel.selectedSizeFilter.value)
            // Price range filter
            val matchPrice = product.price >= viewModel.minPriceFilter.value && product.price <= viewModel.maxPriceFilter.value

            matchCategory && matchQuery && matchSize && matchPrice
        }.sortedWith { a, b ->
            when (viewModel.sortOption.value) {
                "Price: Low to High" -> a.price.compareTo(b.price)
                "Price: High to Low" -> b.price.compareTo(a.price)
                "Top Rated" -> b.averageRating.compareTo(a.averageRating)
                else -> 0 // Default
            }
        }

        if (filteredProducts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "No items found",
                        tint = SoftGrey,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("No matching Superdry products found.", fontWeight = FontWeight.Bold, color = SoftGrey)
                    Text("Try broadening your search or resetting filters.", fontSize = 13.sp, color = SoftGrey)
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(filteredProducts) { product ->
                    ProductCompactCard(product) {
                        viewModel.navigateTo(Screen.ProductDetail(product.id))
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. PRODUCT DETAIL PAGE
// ==========================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProductDetailScreen(viewModel: SuperdryViewModel, productId: String) {
    val context = LocalContext.current
    var product by remember { mutableStateOf<ProductEntity?>(null) }
    
    // Load product reactively
    val allProducts by viewModel.productsState.collectAsState()
    LaunchedEffect(productId, allProducts) {
        product = allProducts.find { it.id == productId }
        viewModel.loadReviews(productId)
    }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val prod = product!!
    val reviewsMap by viewModel.productReviews.collectAsState()
    val productReviewsList = reviewsMap[productId] ?: emptyList()
    val wishlistItems by viewModel.wishlistItemsState.collectAsState()
    val isWishlisted = wishlistItems.any { it.productId == productId }

    var selectedSize by remember { mutableStateOf(prod.sizesCsv.split(",").firstOrNull() ?: "M") }
    var selectedColor by remember { mutableStateOf(prod.colorsCsv.split(",").firstOrNull() ?: "Jet Black") }
    
    // Write review dialog
    var showReviewDialog by remember { mutableStateOf(false) }
    var ratingInput by remember { mutableStateOf(5) }
    var reviewCommentInput by remember { mutableStateOf("") }
    var reviewerNameInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Main Product Image with Wishlist Toggle & Back button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
        ) {
            Image(
                painter = getProductImagePainter(prod.imageResName),
                contentDescription = prod.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Header actions overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                FilledIconButton(
                    onClick = { viewModel.popBackStack() },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f))
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                }

                FilledIconButton(
                    onClick = { viewModel.toggleWishlist(prod.id) },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("add_to_wishlist_button")
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) Color.Red else Color.White
                    )
                }
            }
        }

        Column(modifier = Modifier.padding(24.dp)) {
            // Category tag and share button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prod.category.uppercase(),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                
                IconButton(onClick = {
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, "Check out the ${prod.name} from Superdry for $${prod.price}!")
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share", tint = SoftGrey)
                }
            }

            // Title
            Text(
                text = prod.name,
                fontSize = 24.sp,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // Price & Stock
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "$${String.format("%.2f", prod.price)}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )

                // Stock availability badge
                val stockStatus = when {
                    prod.availableStock <= 0 -> "OUT OF STOCK" to Color.Red
                    prod.availableStock <= 5 -> "ONLY ${prod.availableStock} LEFT" to Color(0xFFFF9900)
                    else -> "IN STOCK" to Color(0xFF00AA55)
                }
                Surface(
                    color = stockStatus.second.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(4.dp),
                    border = BorderStroke(1.dp, stockStatus.second)
                ) {
                    Text(
                        text = stockStatus.first,
                        color = stockStatus.second,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Description
            Text(
                text = prod.description,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(vertical = 12.dp),
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
            )

            // Available Colours Grid Selection
            Text(
                text = "SELECT COLOUR",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                prod.colorsCsv.split(",").forEach { color ->
                    val isSelected = selectedColor == color
                    OutlinedButton(
                        onClick = { selectedColor = color },
                        border = BorderStroke(1.5.dp, if (isSelected) MaterialTheme.colorScheme.primary else BorderGrey),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent
                        ),
                        shape = RoundedCornerShape(2.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = color,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Available Sizes Grid Selection
            Text(
                text = "SELECT SIZE",
                fontWeight = FontWeight.Black,
                fontSize = 12.sp,
                letterSpacing = 1.sp,
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                prod.sizesCsv.split(",").forEach { size ->
                    val isSelected = selectedSize == size
                    OutlinedButton(
                        onClick = { selectedSize = size },
                        border = BorderStroke(1.5.dp, if (isSelected) MaterialTheme.colorScheme.primary else BorderGrey),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.05f) else Color.Transparent
                        ),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.sizeIn(minWidth = 50.dp, minHeight = 44.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text(
                            text = size,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Add to basket main CTA
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (prod.availableStock > 0) {
                        viewModel.addToCart(prod.id, selectedSize, selectedColor)
                        Toast.makeText(context, "${prod.name} added to Basket!", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = prod.availableStock > 0,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("add_to_cart_button"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(2.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Basket")
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = if (prod.availableStock > 0) "ADD TO BASKET" else "OUT OF STOCK",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        letterSpacing = 1.sp
                    )
                }
            }

            // AI Consultation shortcut
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SupportAgent,
                        contentDescription = "AI Stylist",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Not sure about the size?", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text("Ask our AI Shopping Assistant to find the perfect fit.", fontSize = 11.sp)
                    }
                    TextButton(onClick = {
                        viewModel.chatMessages // Touch chat messages
                        viewModel.navigateTo(Screen.AIAssistant)
                    }) {
                        Text("ASK AI", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    }
                }
            }

            // RELATED PRODUCTS SECTION
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "MATCH IT WITH",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                letterSpacing = 1.5.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
            val relatedProducts = allProducts.filter { it.category != prod.category }.take(3)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                relatedProducts.forEach { item ->
                    ProductCompactCard(item) {
                        viewModel.navigateTo(Screen.ProductDetail(item.id))
                    }
                }
            }

            // CUSTOMER REVIEWS SECTION
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "CUSTOMER REVIEWS (${productReviewsList.size})",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    letterSpacing = 1.5.sp
                )
                TextButton(onClick = { showReviewDialog = true }) {
                    Text("+ WRITE REVIEW", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }

            // Review overview metrics
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = prod.averageRating.toString(),
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = if (index < prod.averageRating.toInt()) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = "Rating Star",
                                tint = LuxuryGold,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    Text("Out of 5 stars based on customer purchases", fontSize = 11.sp, color = SoftGrey)
                }
            }

            // Individual review items
            if (productReviewsList.isEmpty()) {
                Text(
                    text = "No reviews yet. Be the first to share your experience with this item!",
                    fontSize = 13.sp,
                    color = SoftGrey,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            } else {
                productReviewsList.forEach { rev ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, BorderGrey),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(rev.reviewerName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Row {
                                    repeat(rev.rating) {
                                        Icon(imageVector = Icons.Default.Star, contentDescription = "*", tint = LuxuryGold, modifier = Modifier.size(12.dp))
                                    }
                                }
                            }
                            Text(rev.comment, fontSize = 13.sp, modifier = Modifier.padding(vertical = 8.dp))
                            
                            // Mark helpful triggers
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("Verified Purchase", fontSize = 11.sp, color = SoftGrey, fontWeight = FontWeight.Bold)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("${rev.isHelpfulCount} people found this helpful", fontSize = 11.sp, color = SoftGrey)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    TextButton(
                                        onClick = { viewModel.markReviewHelpful(rev.id, prod.id) },
                                        contentPadding = PaddingValues(horizontal = 12.dp)
                                    ) {
                                        Text("Helpful", fontSize = 11.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Write Review Dialog
    if (showReviewDialog) {
        Dialog(onDismissRequest = { showReviewDialog = false }) {
            Card(
                shape = RoundedCornerShape(4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text("SHARE YOUR FEEDBACK", fontWeight = FontWeight.Black, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Rating out of 5:", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        (1..5).forEach { r ->
                            val isSelected = ratingInput == r
                            FilledIconButton(
                                onClick = { ratingInput = r },
                                colors = IconButtonDefaults.filledIconButtonColors(
                                    containerColor = if (isSelected) MaterialTheme.colorScheme.primary else BorderGrey
                                ),
                                modifier = Modifier.size(40.dp)
                            ) {
                                Text(r.toString(), fontWeight = FontWeight.Bold, color = if (isSelected) Color.White else Color.Black)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = reviewerNameInput,
                        onValueChange = { reviewerNameInput = it },
                        label = { Text("Your Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = reviewCommentInput,
                        onValueChange = { reviewCommentInput = it },
                        label = { Text("Write your review") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        maxLines = 4
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showReviewDialog = false }) {
                            Text("CANCEL", color = SoftGrey)
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Button(
                            onClick = {
                                if (reviewerNameInput.isNotBlank() && reviewCommentInput.isNotBlank()) {
                                    viewModel.submitReview(prod.id, ratingInput, reviewerNameInput, reviewCommentInput)
                                    showReviewDialog = false
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(2.dp)
                        ) {
                            Text("SUBMIT", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. SHOPPING BASKET
// ==========================================
@Composable
fun BasketScreen(viewModel: SuperdryViewModel, products: List<ProductEntity>, cartItems: List<CartItemEntity>) {
    val itemsInCart = cartItems.mapNotNull { item ->
        val prod = products.find { it.id == item.productId }
        if (prod != null) prod to item else null
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        if (itemsInCart.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(24.dp)) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = "Empty Basket",
                        tint = SoftGrey,
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("YOUR SHOPPING BASKET IS EMPTY", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp)
                    Text(
                        text = "Explore our premium jackets, windcheaters, t-shirts, and collections to add items.",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = SoftGrey,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                    Button(
                        onClick = { viewModel.clearToHome() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("EXPLORE PRODUCTS", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            // Cart lists
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text("MY SHOPPING BASKET", fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 1.sp)
                }

                items(itemsInCart) { (product, cartItem) ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, BorderGrey),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Image(
                                painter = getProductImagePainter(product.imageResName),
                                contentDescription = product.name,
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                            )

                            Spacer(modifier = Modifier.width(16.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "Size: ${cartItem.selectedSize} | Color: ${cartItem.selectedColor}",
                                    fontSize = 11.sp,
                                    color = SoftGrey,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "$${String.format("%.2f", product.price)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Black,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(vertical = 4.dp)
                                )

                                // Quantity selector
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.background(BorderGrey.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.updateCartQuantity(cartItem.id, cartItem.quantity - 1) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                        }
                                        Text(
                                            text = cartItem.quantity.toString(),
                                            fontWeight = FontWeight.Black,
                                            fontSize = 13.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )
                                        IconButton(
                                            onClick = { viewModel.updateCartQuantity(cartItem.id, cartItem.quantity + 1) },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(imageVector = Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    IconButton(onClick = { viewModel.removeFromCart(cartItem.id) }) {
                                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Remove", tint = Color.Red.copy(alpha = 0.8f))
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Summary Bottom Panel
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp)
                ) {
                    // Promo Code Input
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = viewModel.promoCode.value,
                            onValueChange = { viewModel.promoCode.value = it },
                            placeholder = { Text("Enter Coupon Code") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(2.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = MaterialTheme.colorScheme.primary,
                                unfocusedBorderColor = BorderGrey
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Button(
                            onClick = { viewModel.applyPromo() },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(2.dp),
                            modifier = Modifier.testTag("apply_promo_button")
                        ) {
                            Text("APPLY", fontWeight = FontWeight.Bold)
                        }
                    }

                    // Feedbacks for coupons
                    viewModel.activePromoError.value?.let { err ->
                        Text(err, color = Color.Red, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    viewModel.promoAppliedSuccess.value?.let { success ->
                        Text(success, color = Color(0xFF00AA55), fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 8.dp))
                    }

                    // Estimations & Totals
                    val subtotal = itemsInCart.sumOf { it.first.price * it.second.quantity }
                    val discount = if (viewModel.activeDiscountPercent.value > 0) subtotal * (viewModel.activeDiscountPercent.value.toDouble() / 100.0) else 0.0
                    val shipping = 4.99 // Flat rate
                    val total = subtotal - discount + shipping

                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Subtotal:", fontSize = 14.sp, color = SoftGrey)
                        Text("$${String.format("%.2f", subtotal)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    if (discount > 0.0) {
                        Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Discount:", fontSize = 14.sp, color = Color(0xFF00AA55))
                            Text("-$${String.format("%.2f", discount)}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFF00AA55))
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Estimated Shipping:", fontSize = 14.sp, color = SoftGrey)
                        Text("$${String.format("%.2f", shipping)}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Text("Total Price:", fontSize = 18.sp, fontWeight = FontWeight.Black)
                        Text("$${String.format("%.2f", total)}", fontSize = 22.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.performCheckout(
                                shippingAddress = "22 Regent Street, London, W1B 5TG, UK",
                                itemsInCart = itemsInCart
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("checkout_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Default.Lock, contentDescription = "Checkout Securely")
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("CHECKOUT SECURELY", fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.sp)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. WISHLIST SCREEN
// ==========================================
@Composable
fun WishlistScreen(viewModel: SuperdryViewModel, products: List<ProductEntity>, wishlistItems: List<WishlistItemEntity>) {
    val context = LocalContext.current
    val itemsInWishlist = wishlistItems.mapNotNull { item -> products.find { it.id == item.productId } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("MY WISHLIST", fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 1.sp)
            if (itemsInWishlist.isNotEmpty()) {
                IconButton(onClick = {
                    val shareStr = "Superdry Wishlist:\n" + itemsInWishlist.joinToString("\n") { "- ${it.name}: $${it.price}" }
                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareStr)
                        type = "text/plain"
                    }
                    context.startActivity(Intent.createChooser(sendIntent, null))
                }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share Wishlist")
                }
            }
        }

        if (itemsInWishlist.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = "Empty Wishlist",
                        tint = SoftGrey,
                        modifier = Modifier.size(96.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Text("YOUR WISHLIST IS EMPTY", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 1.sp)
                    Text(
                        text = "Save items to your wishlist and shop them whenever you are ready.",
                        textAlign = TextAlign.Center,
                        fontSize = 13.sp,
                        color = SoftGrey,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                    )
                    Button(
                        onClick = { viewModel.clearToHome() },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(2.dp),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("START SHOPPING", fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(itemsInWishlist) { prod ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column {
                            Box(modifier = Modifier.height(150.dp).fillMaxWidth()) {
                                Image(
                                    painter = getProductImagePainter(prod.imageResName),
                                    contentDescription = prod.name,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                IconButton(
                                    onClick = { viewModel.toggleWishlist(prod.id) },
                                    modifier = Modifier.align(Alignment.TopEnd)
                                ) {
                                    Icon(imageVector = Icons.Default.Cancel, contentDescription = "Remove", tint = Color.Black.copy(alpha = 0.7f))
                                }
                            }
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(prod.name, fontWeight = FontWeight.Bold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text("$${prod.price}", fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.primary)
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = {
                                        viewModel.addToCart(prod.id, "M", "Jet Black")
                                        viewModel.toggleWishlist(prod.id)
                                        Toast.makeText(context, "Moved to Basket!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                    shape = RoundedCornerShape(2.dp),
                                    contentPadding = PaddingValues(vertical = 4.dp)
                                ) {
                                    Text("MOVE TO CART", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. CUSTOMER ACCOUNT & ORDERS
// ==========================================
@Composable
fun AccountScreen(viewModel: SuperdryViewModel, profile: UserProfileEntity?, orders: List<OrderEntity>) {
    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        if (profile == null || !profile.isLoggedIn) {
            // Login / Create Account Form
            Text("WELCOME TO SUPERDRY", fontWeight = FontWeight.Black, fontSize = 24.sp, letterSpacing = 1.5.sp)
            Text("Create an account or login to track orders, save wishlists, and manage fast checkouts.", fontSize = 14.sp, color = SoftGrey, modifier = Modifier.padding(vertical = 8.dp))

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = nameInput,
                onValueChange = { nameInput = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = emailInput,
                onValueChange = { emailInput = it },
                label = { Text("Email Address") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = MaterialTheme.colorScheme.primary)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (nameInput.isNotBlank() && emailInput.isNotBlank()) {
                        viewModel.login(emailInput, nameInput)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(2.dp)
            ) {
                Text("LOGIN / SIGN UP", fontWeight = FontWeight.Black, fontSize = 14.sp)
            }
        } else {
            // Logged In Dashboard
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("HELLO, ${profile.name.uppercase()}", fontWeight = FontWeight.Black, fontSize = 22.sp, letterSpacing = 1.sp)
                    Text(profile.email, fontSize = 13.sp, color = SoftGrey)
                }
                IconButton(onClick = { viewModel.logout() }) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = "Logout", tint = Color.Red)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Profile info card
            Text("PERSONAL INFORMATION", fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.sp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                border = BorderStroke(1.dp, BorderGrey),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(4.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Saved Delivery Address:", fontSize = 12.sp, color = SoftGrey, fontWeight = FontWeight.Bold)
                    }
                    Text(profile.address, fontSize = 14.sp, modifier = Modifier.padding(vertical = 4.dp))
                    Divider(modifier = Modifier.padding(vertical = 12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Saved Payment Method:", fontSize = 12.sp, color = SoftGrey, fontWeight = FontWeight.Bold)
                    }
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                        Icon(imageVector = Icons.Default.CreditCard, contentDescription = "Visa", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Visa ending in ${profile.cardEnding}", fontSize = 14.sp)
                    }
                }
            }

            // Orders list
            Spacer(modifier = Modifier.height(24.dp))
            Text("ORDER HISTORY & TRACKING", fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.sp)
            if (orders.isEmpty()) {
                Text("You haven't placed any orders yet. Place an order to see its live tracking here!", fontSize = 13.sp, color = SoftGrey, modifier = Modifier.padding(vertical = 12.dp))
            } else {
                orders.forEach { order ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable { viewModel.navigateTo(Screen.OrderTracking(order.orderId)) },
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, BorderGrey),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(order.orderId, fontWeight = FontWeight.Black, fontSize = 14.sp)
                                Text("Total: $${String.format("%.2f", order.totalAmount)}", fontSize = 13.sp, color = SoftGrey)
                            }
                            Surface(
                                color = when (order.status) {
                                    "Processing" -> Color.Blue.copy(alpha = 0.1f)
                                    "In Transit" -> Color(0xFFFF9900).copy(alpha = 0.1f)
                                    else -> Color(0xFF00AA55).copy(alpha = 0.1f)
                                },
                                contentColor = when (order.status) {
                                    "Processing" -> Color.Blue
                                    "In Transit" -> Color(0xFFFF9900)
                                    else -> Color(0xFF00AA55)
                                },
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = order.status.uppercase(),
                                    fontWeight = FontWeight.Black,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 7. ORDER TRACKING SCREEN
// ==========================================
@Composable
fun OrderTrackingScreen(viewModel: SuperdryViewModel, orderId: String) {
    val orders by viewModel.ordersState.collectAsState()
    val order = orders.find { it.orderId == orderId }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Order $orderId not found.")
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { viewModel.popBackStack() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
            }
            Text("ORDER DETAILS", fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 1.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Order Summary Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderGrey),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Order Number:", fontSize = 12.sp, color = SoftGrey)
                    Text(order.orderId, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Tracking Code:", fontSize = 12.sp, color = SoftGrey)
                    Text(order.trackingNumber, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Estimate Delivery:", fontSize = 12.sp, color = SoftGrey)
                    Text(order.deliveryEstimate, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Shipping To:", fontSize = 12.sp, color = SoftGrey)
                    Text(order.shippingAddress, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.widthIn(max = 200.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Live Delivery Stepper Tracker
        Text("SHIPPING PROGRESS STATUS", fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 1.sp)
        Spacer(modifier = Modifier.height(16.dp))

        val steps = listOf(
            Triple("Order Received", "We have received your payment and are picking your order in London warehouse.", order.status in listOf("Processing", "Dispatched", "In Transit", "Delivered")),
            Triple("Quality Checked & Dispatched", "Your package passed final material inspections and is handed to DPD.", order.status in listOf("Dispatched", "In Transit", "Delivered")),
            Triple("With Delivery Courier", "Package arrived at local delivery depot, out for delivery soon.", order.status in listOf("In Transit", "Delivered")),
            Triple("Delivered Successfully", "Signature verified at recipient address.", order.status == "Delivered")
        )

        steps.forEachIndexed { index, step ->
            Row(modifier = Modifier.fillMaxWidth()) {
                // Tracking line & pin
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(36.dp)) {
                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .background(
                                color = if (step.third) MaterialTheme.colorScheme.primary else BorderGrey,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (step.third) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = "Completed", tint = Color.White, modifier = Modifier.size(12.dp))
                        }
                    }
                    if (index < steps.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(60.dp)
                                .background(if (step.third) MaterialTheme.colorScheme.primary else BorderGrey)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // Step Details
                Column(modifier = Modifier.padding(bottom = 24.dp)) {
                    Text(
                        text = step.first,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (step.third) MaterialTheme.colorScheme.onBackground else SoftGrey
                    )
                    Text(
                        text = step.second,
                        fontSize = 12.sp,
                        color = if (step.third) MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f) else SoftGrey
                    )
                }
            }
        }
    }
}

// ==========================================
// 8. STORE LOCATOR SCREEN
// ==========================================
@Composable
fun StoreLocatorScreen(viewModel: SuperdryViewModel, stores: List<StoreEntity>) {
    var activeStore by remember { mutableStateOf<StoreEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // High Quality Map Canvas / Customized map image representing locator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_store_map),
                contentDescription = "Interactive London Flagship Map",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Stylized pointer badge overlays for active store
            activeStore?.let { store ->
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(bottom = 40.dp)
                ) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(4.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Text(
                            text = store.name,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Stores listings header
        Text(
            text = "SUPERDRY GLOBAL FLAGSHIP STORES",
            fontWeight = FontWeight.Black,
            fontSize = 14.sp,
            letterSpacing = 1.sp,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(stores) { store ->
                val isSelected = activeStore?.id == store.id
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { activeStore = store },
                    border = BorderStroke(1.5.dp, if (isSelected) MaterialTheme.colorScheme.primary else BorderGrey),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(store.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Icon(
                                imageVector = if (isSelected) Icons.Default.Place else Icons.Default.LocationOn,
                                contentDescription = "Location",
                                tint = if (isSelected) MaterialTheme.colorScheme.primary else SoftGrey
                            )
                        }
                        Text(store.address, fontSize = 13.sp, color = SoftGrey, modifier = Modifier.padding(vertical = 4.dp))
                        
                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        Text("Opening Hours:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftGrey)
                        Text(store.hours, fontSize = 12.sp, lineHeight = 16.sp)

                        Text("Phone:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = SoftGrey, modifier = Modifier.padding(top = 8.dp))
                        Text(store.phone, fontSize = 12.sp)

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            store.services.split(",").forEach { srv ->
                                Surface(
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    shape = RoundedCornerShape(4.dp)
                                ) {
                                    Text(
                                        text = srv,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 9. AI SHOPPING ASSISTANT
// ==========================================
@Composable
fun AIAssistantScreen(viewModel: SuperdryViewModel) {
    val products by viewModel.productsState.collectAsState()
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Scroll to bottom when new messages arrive
    LaunchedEffect(viewModel.chatMessages.size) {
        if (viewModel.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(viewModel.chatMessages.size - 1)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // AI Header bar with clean controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SupportAgent,
                    contentDescription = "AI Stylist",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("SUPERDRY STYLIST AI", fontWeight = FontWeight.Black, fontSize = 16.sp, letterSpacing = 0.5.sp)
                    Text("Powered by Gemini 3.5 Flash", fontSize = 11.sp, color = SoftGrey)
                }
            }

            TextButton(onClick = { viewModel.clearChat() }) {
                Text("RESET CHAT", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            }
        }

        Divider()

        // Chat Bubble list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(viewModel.chatMessages) { chat ->
                ChatBubble(chat)
            }
            if (viewModel.aiLoading.value) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(8.dp),
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("Curating style recommendations...", fontSize = 12.sp, color = SoftGrey)
                    }
                }
            }
        }

        // Input Message Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = messageText,
                onValueChange = { messageText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Ask for outfits, sizes, or outerwear tips...") },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = BorderGrey
                )
            )

            Spacer(modifier = Modifier.width(12.dp))

            FilledIconButton(
                onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendChatMessage(messageText, products)
                        messageText = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .testTag("send_message_button"),
                shape = CircleShape,
                colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Prompt",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun ChatBubble(msg: ChatMessage) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (msg.isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .background(
                    color = if (msg.isUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (msg.isUser) 16.dp else 0.dp,
                        bottomEnd = if (msg.isUser) 0.dp else 16.dp
                    )
                )
                .padding(14.dp)
        ) {
            Text(
                text = msg.text,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = if (msg.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ==========================================
// 10. ADMIN DASHBOARD SCREEN
// ==========================================
@Composable
fun AdminDashboardScreen(viewModel: SuperdryViewModel, products: List<ProductEntity>) {
    var prodIdInput by remember { mutableStateOf("") }
    var prodNameInput by remember { mutableStateOf("") }
    var prodDescInput by remember { mutableStateOf("") }
    var prodPriceInput by remember { mutableStateOf("") }
    var prodCategoryInput by remember { mutableStateOf("Jackets") }
    var prodColorsInput by remember { mutableStateOf("Jet Black,Superdry Orange") }
    var prodSizesInput by remember { mutableStateOf("S,M,L,XL") }
    var prodStockInput by remember { mutableStateOf("10") }

    val categories = listOf("Jackets", "Hoodies", "T-Shirts", "Jeans", "Footwear", "Accessories")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Text("ADMINISTRATOR BACKEND PANEL", fontWeight = FontWeight.Black, fontSize = 20.sp, letterSpacing = 1.sp)
        Text("Manage catalog products, stock metrics, and operational workflows.", fontSize = 13.sp, color = SoftGrey, modifier = Modifier.padding(bottom = 16.dp))

        // Create product card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderGrey),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ADD NEW PRODUCT", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(value = prodIdInput, onValueChange = { prodIdInput = it }, label = { Text("Product ID (e.g. sd-jacket-99)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = prodNameInput, onValueChange = { prodNameInput = it }, label = { Text("Product Name") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = prodDescInput, onValueChange = { prodDescInput = it }, label = { Text("Product Description") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = prodPriceInput, onValueChange = { prodPriceInput = it }, label = { Text("Price (e.g. 89.99)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                Spacer(modifier = Modifier.height(8.dp))

                // Category Selector Dropdown
                Text("Category Select:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = SoftGrey)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { cat ->
                        val isSelected = prodCategoryInput == cat
                        FilterChip(selected = isSelected, onClick = { prodCategoryInput = cat }, label = { Text(cat) })
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = prodColorsInput, onValueChange = { prodColorsInput = it }, label = { Text("Colors (Comma separated)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = prodSizesInput, onValueChange = { prodSizesInput = it }, label = { Text("Sizes (Comma separated)") }, modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(value = prodStockInput, onValueChange = { prodStockInput = it }, label = { Text("Available Stock") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val price = prodPriceInput.toDoubleOrNull() ?: 0.0
                        val stock = prodStockInput.toIntOrNull() ?: 0
                        if (prodIdInput.isNotBlank() && prodNameInput.isNotBlank() && price > 0.0) {
                            viewModel.addProduct(prodIdInput, prodNameInput, prodDescInput, price, prodCategoryInput, prodColorsInput, prodSizesInput, stock)
                            prodIdInput = ""
                            prodNameInput = ""
                            prodDescInput = ""
                            prodPriceInput = ""
                            Toast.makeText(viewModel.productsState.value.let { products.firstOrNull()?.let { null } } ?: null ?: viewModel.hashCode().let { null } ?: null, "Product saved successfully!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("add_product_admin_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(2.dp)
                ) {
                    Text("ADD TO ACTIVE CATALOG", fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Stock metrics management list
        Text("MANAGE CURRENT STOCK LEVELS", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderGrey),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                products.forEach { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(p.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text("Current Stock: ${p.availableStock}", fontSize = 12.sp, color = SoftGrey)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.updateStock(p.id, p.availableStock - 1) }) {
                                Icon(imageVector = Icons.Default.RemoveCircleOutline, contentDescription = "Reduce Stock", tint = Color.Red)
                            }
                            IconButton(onClick = { viewModel.updateStock(p.id, p.availableStock + 1) }) {
                                Icon(imageVector = Icons.Default.AddCircleOutline, contentDescription = "Increase Stock", tint = Color(0xFF00AA55))
                            }
                        }
                    }
                    Divider()
                }
            }
        }
    }
}
