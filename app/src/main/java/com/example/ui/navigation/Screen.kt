package com.example.ui.navigation

sealed class Screen(val title: String) {
    object Home : Screen("Home")
    object Shop : Screen("Shop")
    data class ProductDetail(val productId: String) : Screen("Product Detail")
    object Basket : Screen("Shopping Basket")
    object Wishlist : Screen("Wishlist")
    object Account : Screen("Account")
    object StoreLocator : Screen("Store Locator")
    object AIAssistant : Screen("AI Assistant")
    object AdminDashboard : Screen("Admin Dashboard")
    data class OrderTracking(val orderId: String) : Screen("Track Order")
}
