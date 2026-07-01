package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: String, // "Jackets", "Hoodies", "T-Shirts", "Jeans", "Footwear", "Accessories"
    val imageResName: String, // e.g. "img_hero_banner" or other drawables
    val sizesCsv: String = "S,M,L,XL",
    val colorsCsv: String = "Jet Black,Superdry Orange,Slate Grey",
    val availableStock: Int = 10,
    val isFeatured: Boolean = false,
    val isNewArrival: Boolean = false,
    val isSale: Boolean = false,
    val discountPercent: Int = 0,
    val averageRating: Double = 4.5
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey val name: String,
    val description: String
)

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val quantity: Int,
    val selectedSize: String,
    val selectedColor: String
)

@Entity(tableName = "wishlist_items")
data class WishlistItemEntity(
    @PrimaryKey val productId: String
)

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val orderId: String,
    val status: String, // "Processing", "Dispatched", "In Transit", "Delivered"
    val date: Long,
    val totalAmount: Double,
    val trackingNumber: String,
    val deliveryEstimate: String,
    val shippingAddress: String
)

@Entity(tableName = "order_items")
data class OrderItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val orderId: String,
    val productId: String,
    val productName: String,
    val price: Double,
    val quantity: Int,
    val size: String,
    val color: String
)

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val productId: String,
    val rating: Int,
    val reviewerName: String,
    val comment: String,
    val date: Long = System.currentTimeMillis(),
    val isHelpfulCount: Int = 0,
    val photoUri: String? = null
)

@Entity(tableName = "stores")
data class StoreEntity(
    @PrimaryKey val id: String,
    val name: String,
    val address: String,
    val hours: String,
    val phone: String,
    val services: String, // Csv: "Click & Collect,Personal Styling"
    val latitude: Double,
    val longitude: Double
)

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val email: String,
    val name: String,
    val phone: String = "",
    val address: String = "",
    val cardEnding: String = "4321",
    val isLoggedIn: Boolean = false
)
