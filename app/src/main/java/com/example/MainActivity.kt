package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.local.AppDatabase
import com.example.data.repository.SuperdryRepository
import com.example.ui.navigation.Screen
import com.example.ui.screens.*
import com.example.ui.theme.*
import com.example.ui.viewmodel.SuperdryViewModel

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: SuperdryRepository
    private lateinit var viewModel: SuperdryViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Instantiate Room Database and Repository
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "superdry_app_db"
        ).fallbackToDestructiveMigration().build()

        repository = SuperdryRepository(database.superdryDao())

        // Use custom factory to instantiate our ViewModel
        val factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                @Suppress("UNCHECKED_CAST")
                return SuperdryViewModel(repository) as T
            }
        }
        viewModel = ViewModelProvider(this, factory)[SuperdryViewModel::class.java]

        setContent {
            MyApplicationTheme {
                val products by viewModel.productsState.collectAsState()
                val categories by viewModel.categoriesState.collectAsState()
                val cartItems by viewModel.cartItemsState.collectAsState()
                val wishlistItems by viewModel.wishlistItemsState.collectAsState()
                val orders by viewModel.ordersState.collectAsState()
                val stores by viewModel.storesState.collectAsState()
                val profile by viewModel.userProfileState.collectAsState()

                Scaffold(
                    topBar = {
                        SuperdryTopBar(
                            currentScreen = viewModel.currentScreen,
                            onStoreLocatorClick = { viewModel.navigateTo(Screen.StoreLocator) },
                            onAdminClick = { viewModel.navigateTo(Screen.AdminDashboard) }
                        )
                    },
                    bottomBar = {
                        SuperdryBottomNavigation(
                            currentScreen = viewModel.currentScreen,
                            cartCount = cartItems.sumOf { it.quantity },
                            wishlistCount = wishlistItems.size,
                            onNavigate = { screen ->
                                if (screen == Screen.Home) {
                                    viewModel.clearToHome()
                                } else {
                                    viewModel.navigateTo(screen)
                                }
                            }
                        )
                    },
                    modifier = Modifier.fillMaxSize(),
                    contentWindowInsets = WindowInsets.safeDrawing
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        // Core Screen Switching Router
                        when (val screen = viewModel.currentScreen) {
                            is Screen.Home -> HomeScreen(viewModel, products)
                            is Screen.Shop -> ShopScreen(viewModel, products, categories)
                            is Screen.ProductDetail -> ProductDetailScreen(viewModel, screen.productId)
                            is Screen.Basket -> BasketScreen(viewModel, products, cartItems)
                            is Screen.Wishlist -> WishlistScreen(viewModel, products, wishlistItems)
                            is Screen.Account -> AccountScreen(viewModel, profile, orders)
                            is Screen.StoreLocator -> StoreLocatorScreen(viewModel, stores)
                            is Screen.AIAssistant -> AIAssistantScreen(viewModel)
                            is Screen.AdminDashboard -> AdminDashboardScreen(viewModel, products)
                            is Screen.OrderTracking -> OrderTrackingScreen(viewModel, screen.orderId)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuperdryTopBar(
    currentScreen: Screen,
    onStoreLocatorClick: () -> Unit,
    onAdminClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "EST. 2003",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.sp,
                    color = SoftGrey
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "SUPERDRY",
                        fontWeight = FontWeight.Black,
                        fontSize = 19.sp,
                        letterSpacing = 1.5.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "®",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )
                }
            }
        },
        actions = {
            IconButton(onClick = onStoreLocatorClick) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Store Locator",
                    tint = if (currentScreen == Screen.StoreLocator) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }
            IconButton(onClick = onAdminClick) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Panel",
                    tint = if (currentScreen == Screen.AdminDashboard) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun SuperdryBottomNavigation(
    currentScreen: Screen,
    cartCount: Int,
    wishlistCount: Int,
    onNavigate: (Screen) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        windowInsets = WindowInsets.navigationBars
    ) {
        NavigationBarItem(
            selected = currentScreen is Screen.Home,
            onClick = { onNavigate(Screen.Home) },
            icon = { Icon(imageVector = Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Shop,
            onClick = { onNavigate(Screen.Shop) },
            icon = { Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = "Shop") },
            label = { Text("Shop", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.AIAssistant,
            onClick = { onNavigate(Screen.AIAssistant) },
            icon = { Icon(imageVector = Icons.Default.SupportAgent, contentDescription = "AI Stylist") },
            label = { Text("AI Stylist", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            modifier = Modifier.testTag("ai_tab"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Wishlist,
            onClick = { onNavigate(Screen.Wishlist) },
            icon = {
                BadgedBox(
                    badge = {
                        if (wishlistCount > 0) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text(wishlistCount.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.Favorite, contentDescription = "Wishlist")
                }
            },
            label = { Text("Wishlist", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            modifier = Modifier.testTag("wishlist_tab"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Basket,
            onClick = { onNavigate(Screen.Basket) },
            icon = {
                BadgedBox(
                    badge = {
                        if (cartCount > 0) {
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text(cartCount.toString(), color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(imageVector = Icons.Default.ShoppingCart, contentDescription = "Basket")
                }
            },
            label = { Text("Basket", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            modifier = Modifier.testTag("basket_tab"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )

        NavigationBarItem(
            selected = currentScreen is Screen.Account,
            onClick = { onNavigate(Screen.Account) },
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Account") },
            label = { Text("Account", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            modifier = Modifier.testTag("account_tab"),
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = Color.White,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary
            )
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
