package com.example.ui.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.SuperdryRepository
import com.example.network.GeminiClient
import com.example.ui.navigation.Screen
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

class SuperdryViewModel(private val repository: SuperdryRepository) : ViewModel() {

    // Initialize database
    init {
        viewModelScope.launch {
            repository.pre_populateIfEmpty()
        }
    }

    // Flows from database
    val productsState: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categoriesState: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cartItemsState: StateFlow<List<CartItemEntity>> = repository.cartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val wishlistItemsState: StateFlow<List<WishlistItemEntity>> = repository.wishlistItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val ordersState: StateFlow<List<OrderEntity>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val storesState: StateFlow<List<StoreEntity>> = repository.allStores
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfileState: StateFlow<UserProfileEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Dynamic product reviews map
    private val _productReviews = MutableStateFlow<Map<String, List<ReviewEntity>>>(emptyMap())
    val productReviews: StateFlow<Map<String, List<ReviewEntity>>> = _productReviews.asStateFlow()

    fun loadReviews(productId: String) {
        viewModelScope.launch {
            repository.getReviewsForProduct(productId).collect { reviews ->
                val current = _productReviews.value.toMutableMap()
                current[productId] = reviews
                _productReviews.value = current
            }
        }
    }

    // Navigation Stack State
    private val _navigationStack = mutableStateOf<List<Screen>>(listOf(Screen.Home))
    val currentScreen: Screen get() = _navigationStack.value.lastOrNull() ?: Screen.Home
    val canPop: Boolean get() = _navigationStack.value.size > 1

    fun navigateTo(screen: Screen) {
        // Prevent duplicate consecutive screens
        if (_navigationStack.value.lastOrNull() == screen) return
        val current = _navigationStack.value.toMutableList()
        current.add(screen)
        _navigationStack.value = current
    }

    fun popBackStack() {
        if (canPop) {
            val current = _navigationStack.value.toMutableList()
            current.removeAt(current.size - 1)
            _navigationStack.value = current
        }
    }

    fun clearToHome() {
        _navigationStack.value = listOf(Screen.Home)
    }

    // Filter, Search, and Sort State
    val searchQuery = mutableStateOf("")
    val selectedCategoryFilter = mutableStateOf<String?>(null)
    val selectedSizeFilter = mutableStateOf<String?>(null)
    val selectedColorFilter = mutableStateOf<String?>(null)
    val minPriceFilter = mutableStateOf(0f)
    val maxPriceFilter = mutableStateOf(200f)
    val sortOption = mutableStateOf("Default") // "Default", "Price: Low to High", "Price: High to Low", "Top Rated"

    // Cart promo code and totals
    val promoCode = mutableStateOf("")
    val activeDiscountPercent = mutableStateOf(0) // e.g. 20 for 20%
    val activePromoError = mutableStateOf<String?>(null)
    val promoAppliedSuccess = mutableStateOf<String?>(null)

    fun applyPromo() {
        val code = promoCode.value.trim().uppercase()
        if (code == "SUPERDRY20") {
            activeDiscountPercent.value = 20
            activePromoError.value = null
            promoAppliedSuccess.value = "SUPERDRY20 (20% Off) applied successfully!"
        } else if (code.isNotEmpty()) {
            activeDiscountPercent.value = 0
            activePromoError.value = "Invalid Promo Code. Try 'SUPERDRY20'."
            promoAppliedSuccess.value = null
        }
    }

    fun removePromo() {
        promoCode.value = ""
        activeDiscountPercent.value = 0
        activePromoError.value = null
        promoAppliedSuccess.value = null
    }

    // Wishlist toggle
    fun toggleWishlist(productId: String) {
        viewModelScope.launch {
            repository.toggleWishlist(productId)
        }
    }

    // Cart actions
    fun addToCart(productId: String, size: String, color: String, quantity: Int = 1) {
        viewModelScope.launch {
            repository.addToCart(productId, size, color, quantity)
        }
    }

    fun updateCartQuantity(cartId: Int, quantity: Int) {
        viewModelScope.launch {
            repository.updateCartQuantity(cartId, quantity)
        }
    }

    fun removeFromCart(cartId: Int) {
        viewModelScope.launch {
            repository.removeFromCart(cartId)
        }
    }

    // Checkout operation
    val checkoutSuccessState = mutableStateOf<OrderEntity?>(null)

    fun performCheckout(shippingAddress: String, itemsInCart: List<Pair<ProductEntity, CartItemEntity>>) {
        viewModelScope.launch {
            val discountCodeStr = if (activeDiscountPercent.value > 0) "SUPERDRY20" else ""
            val order = repository.checkout(shippingAddress, itemsInCart, discountCodeStr)
            checkoutSuccessState.value = order
            removePromo() // Clear discount after checkout
            navigateTo(Screen.OrderTracking(order.orderId))
        }
    }

    fun clearCheckoutState() {
        checkoutSuccessState.value = null
    }

    // User Login/Logout Actions
    fun login(email: String, name: String) {
        viewModelScope.launch {
            repository.loginUser(email, name)
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logoutUser()
        }
    }

    // Submit user product review
    fun submitReview(productId: String, rating: Int, reviewer: String, comment: String) {
        viewModelScope.launch {
            repository.addReview(productId, rating, reviewer, comment)
            loadReviews(productId) // Reload reviews
        }
    }

    fun markReviewHelpful(reviewId: Int, productId: String) {
        viewModelScope.launch {
            repository.markReviewHelpful(reviewId)
            loadReviews(productId)
        }
    }

    // AI Shopping Assistant State
    private val _chatMessages = mutableStateListOf<ChatMessage>().apply {
        add(ChatMessage("initial", "Hello! I am your personal Superdry Stylist AI. I can recommend outfits, suggest sizes, or find matching items from our premium Japanese-inspired collections. Ask me anything!", false))
    }
    val chatMessages: List<ChatMessage> get() = _chatMessages
    val aiLoading = mutableStateOf(false)

    fun sendChatMessage(text: String, currentProducts: List<ProductEntity>) {
        if (text.isBlank()) return
        val userMsg = ChatMessage(text = text, isUser = true)
        _chatMessages.add(userMsg)
        
        viewModelScope.launch {
            aiLoading.value = true
            
            // Generate catalog context for Gemini
            val catalogContext = currentProducts.joinToString("\n") { p ->
                "- [${p.id}] ${p.name} ($${p.price}) in ${p.category}. Colors: ${p.colorsCsv}. Sizes: ${p.sizesCsv}. Available: ${p.availableStock} items."
            }

            val systemInstruction = """
                You are a premium luxury fashion stylist and consultant for the global retail clothing brand Superdry.
                You help customers with sizing, coordinating outfits, matching accessories/outerwear, and product questions.
                
                Always speak in an elegant, modern, luxury tone. Use clear headings or bullet points where appropriate.
                Reference the following real product catalog to make accurate product recommendations:
                $catalogContext
                
                If the user asks for size recommendations, ask or factor in their typical fits, and suggest sizing up for a relaxed fit, since Superdry jackets run fitted.
                If they ask for outfit suggestions, combine 3-4 actual items from the list above.
                If they ask about stock, look up the available stock from the catalog.
                
                Keep answers structured, and match the Superdry premium identity.
            """.trimIndent()

            // Map chat messages to raw history pairs (text, isUser)
            val historyList = _chatMessages.dropLast(1).map { it.text to it.isUser }

            val response = GeminiClient.askAssistant(text, systemInstruction, historyList)
            
            _chatMessages.add(ChatMessage(text = response, isUser = false))
            aiLoading.value = false
        }
    }

    fun clearChat() {
        _chatMessages.clear()
        _chatMessages.add(ChatMessage("initial", "Hello! I am your personal Superdry Stylist AI. I can recommend outfits, suggest sizes, or find matching items from our premium Japanese-inspired collections. Ask me anything!", false))
    }

    // Admin Dashboard Operations
    fun addProduct(
        id: String,
        name: String,
        description: String,
        price: Double,
        category: String,
        colors: String,
        sizes: String,
        stock: Int
    ) {
        viewModelScope.launch {
            val newProduct = ProductEntity(
                id = id,
                name = name,
                description = description,
                price = price,
                category = category,
                imageResName = "img_collection_women", // default collection image
                sizesCsv = sizes,
                colorsCsv = colors,
                availableStock = stock,
                isNewArrival = true
            )
            repository.saveProduct(newProduct)
        }
    }

    fun updateStock(productId: String, newStock: Int) {
        viewModelScope.launch {
            val product = repository.getProductById(productId)
            if (product != null) {
                repository.saveProduct(product.copy(availableStock = newStock))
            }
        }
    }
}
