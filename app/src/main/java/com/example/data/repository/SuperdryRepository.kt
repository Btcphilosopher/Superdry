package com.example.data.repository

import com.example.data.local.SuperdryDao
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import java.util.UUID

class SuperdryRepository(private val dao: SuperdryDao) {

    // Exposure of Flows and functions
    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val allCategories: Flow<List<CategoryEntity>> = dao.getAllCategories()
    val cartItems: Flow<List<CartItemEntity>> = dao.getCartItems()
    val wishlistItems: Flow<List<WishlistItemEntity>> = dao.getWishlistItems()
    val allOrders: Flow<List<OrderEntity>> = dao.getAllOrders()
    val allStores: Flow<List<StoreEntity>> = dao.getAllStores()
    val userProfile: Flow<UserProfileEntity?> = dao.getUserProfile()

    suspend fun getProductById(id: String): ProductEntity? = dao.getProductById(id)
    suspend fun getOrderItems(orderId: String): List<OrderItemEntity> = dao.getOrderItems(orderId)
    fun getReviewsForProduct(productId: String): Flow<List<ReviewEntity>> = dao.getReviewsForProduct(productId)
    fun isInWishlist(productId: String): Flow<Boolean> = dao.isInWishlist(productId)

    // Admin action: Add or edit product
    suspend fun saveProduct(product: ProductEntity) {
        dao.insertProducts(listOf(product))
    }

    // Shopping actions
    suspend fun addToCart(productId: String, size: String, color: String, quantity: Int = 1) {
        val currentItems = dao.getCartItems().firstOrNull() ?: emptyList()
        val existing = currentItems.find { it.productId == productId && it.selectedSize == size && it.selectedColor == color }
        if (existing != null) {
            dao.updateCartItem(existing.copy(quantity = existing.quantity + quantity))
        } else {
            dao.insertCartItem(CartItemEntity(productId = productId, quantity = quantity, selectedSize = size, selectedColor = color))
        }
    }

    suspend fun updateCartQuantity(cartId: Int, quantity: Int) {
        val currentItems = dao.getCartItems().firstOrNull() ?: emptyList()
        val existing = currentItems.find { it.id == cartId }
        if (existing != null) {
            if (quantity <= 0) {
                dao.deleteCartItem(cartId)
            } else {
                dao.updateCartItem(existing.copy(quantity = quantity))
            }
        }
    }

    suspend fun removeFromCart(cartId: Int) {
        dao.deleteCartItem(cartId)
    }

    suspend fun clearCart() {
        dao.clearCart()
    }

    suspend fun toggleWishlist(productId: String) {
        val current = dao.getWishlistItems().firstOrNull() ?: emptyList()
        val exists = current.any { it.productId == productId }
        if (exists) {
            dao.deleteWishlistItem(productId)
        } else {
            dao.insertWishlistItem(WishlistItemEntity(productId))
        }
    }

    suspend fun addReview(productId: String, rating: Int, reviewer: String, comment: String, photoUri: String? = null) {
        dao.insertReview(
            ReviewEntity(
                productId = productId,
                rating = rating,
                reviewerName = reviewer,
                comment = comment,
                date = System.currentTimeMillis(),
                photoUri = photoUri
            )
        )
        // Recalculate average rating
        val p = dao.getProductById(productId)
        if (p != null) {
            // Mock a slight change in rating
            val newRating = ((p.averageRating * 4) + rating) / 5.0
            dao.updateProduct(p.copy(averageRating = Math.round(newRating * 10) / 10.0))
        }
    }

    suspend fun markReviewHelpful(reviewId: Int) {
        dao.markReviewHelpful(reviewId)
    }

    suspend fun loginUser(email: String, name: String) {
        dao.saveUserProfile(
            UserProfileEntity(
                email = email,
                name = name,
                phone = "+44 7700 900077",
                address = "22 Regent Street, London, W1B 5TG, UK",
                cardEnding = "4321",
                isLoggedIn = true
            )
        )
    }

    suspend fun logoutUser() {
        dao.clearUserProfile()
    }

    suspend fun checkout(shippingAddress: String, itemsInCart: List<Pair<ProductEntity, CartItemEntity>>, discountCode: String = ""): OrderEntity {
        val orderId = "SD-" + UUID.randomUUID().toString().substring(0, 8).uppercase()
        val subtotal = itemsInCart.sumOf { it.first.price * it.second.quantity }
        val discount = if (discountCode.uppercase() == "SUPERDRY20") subtotal * 0.20 else 0.0
        val total = subtotal - discount + 4.99 // Shipping flat fee

        val order = OrderEntity(
            orderId = orderId,
            status = "Processing",
            date = System.currentTimeMillis(),
            totalAmount = total,
            trackingNumber = "GB" + (100000000..999999999).random() + "XP",
            deliveryEstimate = "Estimated 3-5 Working Days",
            shippingAddress = shippingAddress
        )

        dao.insertOrder(order)

        val orderItems = itemsInCart.map { (product, cartItem) ->
            OrderItemEntity(
                orderId = orderId,
                productId = product.id,
                productName = product.name,
                price = product.price,
                quantity = cartItem.quantity,
                size = cartItem.selectedSize,
                color = cartItem.selectedColor
            )
        }
        dao.insertOrderItems(orderItems)
        dao.clearCart()

        return order
    }

    // Database pre-population
    suspend fun pre_populateIfEmpty() {
        val count = dao.getAllProducts().firstOrNull()?.size ?: 0
        if (count > 0) return // Already populated

        // Insert Categories
        val categories = listOf(
            CategoryEntity("Jackets", "Iconic Superdry outerwear featuring premium technical layers, windcheaters, and rugged coats."),
            CategoryEntity("Hoodies", "Classic loopback cotton sweaters, premium overhead styles, and vintage collegiate zip designs."),
            CategoryEntity("T-Shirts", "Sustainable organic cotton graphic tees, timeless vintage scripts, and premium basic layers."),
            CategoryEntity("Jeans", "Expertly crafted Japanese-inspired denim in vintage fits, custom washes, and modern silhouettes."),
            CategoryEntity("Footwear", "Retro cupsole court sneakers, running-inspired athletic lifestyle trainers, and rugged desert boots."),
            CategoryEntity("Accessories", "Highly durable backpacks, classic graphic beanies, premium leather belts, and active wear essentials.")
        )
        dao.insertCategories(categories)

        // Insert Products
        val products = listOf(
            ProductEntity(
                id = "sd-jacket-01",
                name = "SD Ultimate Windcheater",
                description = "Engineered to keep you safe from the elements. Features a triple-layer main zip, high fleece-lined collar with bungee cords, double cuffs with thumbholes, and two secure front zip pockets. Accentuated with signature Superdry orange accents, embroidered logos, and reflective stitching.",
                price = 119.99,
                category = "Jackets",
                imageResName = "img_hero_banner", // Uses our custom generated image!
                sizesCsv = "S,M,L,XL,XXL",
                colorsCsv = "Superdry Orange,Jet Black,Off-White,Vintage Navy",
                availableStock = 12,
                isFeatured = true,
                isNewArrival = true,
                isSale = false,
                averageRating = 4.8
            ),
            ProductEntity(
                id = "sd-jacket-02",
                name = "Everest Premium Parka",
                description = "A warm, high-durability winter essential inspired by mountaineering gear. Complete with thick heavy-duty insulation, a removable faux-fur trim hood with adjustable buckled strap, six cargo style utility pockets, storm flap closure, and ribbed inner cuffs to lock in heat.",
                price = 159.99,
                category = "Jackets",
                imageResName = "img_hero_banner", // Hero fallback
                sizesCsv = "M,L,XL,XXL",
                colorsCsv = "Vintage Khaki,Jet Black,Deep Navy",
                availableStock = 5,
                isFeatured = true,
                isNewArrival = false,
                isSale = false,
                averageRating = 4.7
            ),
            ProductEntity(
                id = "sd-hoodie-01",
                name = "Vintage Logo Script Hoodie",
                description = "Our classic sportswear core hoodie. Features a vintage flocked script logo chest graphic, soft-touch brush-back cotton inner lining, kangaroo pouch pocket, ribbed cuffs, and custom metal-tipped drawstrings. A timeless collegiate fit that pairs perfectly with any outfit.",
                price = 79.99,
                category = "Hoodies",
                imageResName = "img_collection_women", // Use clothing flatlay
                sizesCsv = "XS,S,M,L,XL",
                colorsCsv = "Slate Grey,Vintage Navy,Faded Red,Superdry Orange",
                availableStock = 20,
                isFeatured = false,
                isNewArrival = true,
                isSale = false,
                averageRating = 4.5
            ),
            ProductEntity(
                id = "sd-hoodie-02",
                name = "Orange Label Zip Hoodie",
                description = "An ultra-premium daily essential from the iconic Orange Label collection. Designed in a slim athletic silhouette, with double-layered hood, brushed lining, and minimalist signature logo embroidered on the left chest. Crafted from high-density organic cotton blended yarns.",
                price = 84.99,
                category = "Hoodies",
                imageResName = "img_collection_women",
                sizesCsv = "S,M,L,XL",
                colorsCsv = "Chcoal Marl,Navy Heather,Off-White",
                availableStock = 15,
                isFeatured = false,
                isNewArrival = false,
                isSale = true,
                discountPercent = 20,
                averageRating = 4.6
            ),
            ProductEntity(
                id = "sd-tee-01",
                name = "Vintage Logo Tri T-Shirt",
                description = "Classic Superdry style. A lightweight vintage tri-blend fabric t-shirt, tailored fit, featuring our iconic cracked-ink distressed athletic logo print. A soft-handle luxury feel, perfect for casual layer-based styling.",
                price = 34.99,
                category = "T-Shirts",
                imageResName = "img_collection_women",
                sizesCsv = "S,M,L,XL",
                colorsCsv = "Grey Marl,Jet Black,Navy Blue",
                availableStock = 30,
                isFeatured = false,
                isNewArrival = false,
                isSale = false,
                averageRating = 4.4
            ),
            ProductEntity(
                id = "sd-tee-02",
                name = "Organic Cotton Core Tee",
                description = "Sustainable everyday style. Made from 100% organic cotton grown using non-toxic natural fertilizers. Superbly soft feel with ribbed crew neck, vintage wash finish, and delicate signature orange tab on the right sleeve.",
                price = 29.99,
                category = "T-Shirts",
                imageResName = "img_collection_women",
                sizesCsv = "XS,S,M,L,XL,XXL",
                colorsCsv = "Pure White,Jet Black,Superdry Orange,Olive Green",
                availableStock = 25,
                isFeatured = true,
                isNewArrival = true,
                isSale = false,
                averageRating = 4.3
            ),
            ProductEntity(
                id = "sd-jeans-01",
                name = "Slim Fit Vintage Denim",
                description = "Tailored Japanese style vintage denim. Featuring classic five-pocket styling, heavy-duty metal rivets, leather waistband brand patch, and copper-tint button fly. Specially faded and distressed around the knees and thighs for a authentic worn-in aesthetic.",
                price = 89.99,
                category = "Jeans",
                imageResName = "img_collection_women",
                sizesCsv = "30/32,32/32,34/32,36/32",
                colorsCsv = "Indigo Wash,Mid Blue Distressed,Charcoal Wash",
                availableStock = 8,
                isFeatured = false,
                isNewArrival = false,
                isSale = false,
                averageRating = 4.6
            ),
            ProductEntity(
                id = "sd-footwear-01",
                name = "Sleek Court Trainers",
                description = "Retro-inspired court shoes with a contemporary edge. Upper crafted from premium full-grain leather, combined with soft suede overlays, a heavily cushioned OrthoLite insole, and classic durable gum rubber cupsole. Signature debossed tongue logo.",
                price = 99.99,
                category = "Footwear",
                imageResName = "img_collection_women",
                sizesCsv = "8,9,10,11",
                colorsCsv = "Bright White,Jet Black",
                availableStock = 10,
                isFeatured = true,
                isNewArrival = true,
                isSale = false,
                averageRating = 4.5
            ),
            ProductEntity(
                id = "sd-accessories-01",
                name = "Montana Canvas Backpack",
                description = "Our legendary backpack design. Crafted from rugged 600D heavy-duty canvas, with spacious dual-zipped main compartment, a secure external front pocket, twin side slots with press studs, and heavily padded adjustable shoulder straps with branded details.",
                price = 54.99,
                category = "Accessories",
                imageResName = "img_collection_women",
                sizesCsv = "One Size",
                colorsCsv = "Jet Black,Vintage Navy,Superdry Orange",
                availableStock = 18,
                isFeatured = false,
                isNewArrival = false,
                isSale = true,
                discountPercent = 15,
                averageRating = 4.7
            )
        )
        dao.insertProducts(products)

        // Insert Stores
        val stores = listOf(
            StoreEntity(
                id = "st-london",
                name = "Superdry London Regent Street",
                address = "189-197 Regent Street, London, W1B 4JN, UK",
                hours = "Mon-Sat: 10:00 - 20:00\nSun: 11:30 - 18:00",
                phone = "+44 20 7434 3801",
                services = "Click & Collect,Personal Styling,Tailoring,In-Store Cafe",
                latitude = 51.5123,
                longitude = -0.1396
            ),
            StoreEntity(
                id = "st-tokyo",
                name = "Superdry Shibuya Flagship",
                address = "1-22 Shibuya, Shibuya City, Tokyo, 150-0002, Japan",
                hours = "Everyday: 11:00 - 21:00",
                phone = "+81 3 5468 0101",
                services = "Click & Collect,Custom Embroidery,Tax-Free Shopping",
                latitude = 35.6580,
                longitude = 139.7016
            ),
            StoreEntity(
                id = "st-newyork",
                name = "Superdry New York Soho",
                address = "21 Mercer Street, New York, NY 10013, USA",
                hours = "Mon-Sat: 11:00 - 19:00\nSun: 12:00 - 18:00",
                phone = "+1 212 925 1515",
                services = "Click & Collect,Personal Styling,Curbside Pickup",
                latitude = 40.7208,
                longitude = -74.0016
            )
        )
        dao.insertStores(stores)

        // Insert some Reviews for sd-jacket-01
        dao.insertReview(
            ReviewEntity(
                productId = "sd-jacket-01",
                rating = 5,
                reviewerName = "Liam Henderson",
                comment = "Absolutely brilliant jacket. The triple zipper works perfectly to seal out cold wind, and the orange flecks look amazing. Truly a premium feel.",
                isHelpfulCount = 14
            )
        )
        dao.insertReview(
            ReviewEntity(
                productId = "sd-jacket-01",
                rating = 4,
                reviewerName = "Sarah Jenkins",
                comment = "Extremely warm and comfortable. The fleece lining is very soft. Deducted one star because the sizing runs slightly small - I suggest ordering one size up.",
                isHelpfulCount = 6
            )
        )

        // Save a default profile to make testing order/wishlist seamless!
        dao.saveUserProfile(
            UserProfileEntity(
                email = "tom@ahyx.org",
                name = "Tom Ahhx",
                phone = "+44 7700 900077",
                address = "22 Regent Street, London, W1B 5TG, UK",
                cardEnding = "4321",
                isLoggedIn = true
            )
        )

        // Create a past order to demonstrate tracking out of the box!
        val orderId = "SD-VINTAGE"
        dao.insertOrder(
            OrderEntity(
                orderId = orderId,
                status = "In Transit",
                date = System.currentTimeMillis() - (86400000 * 2), // 2 days ago
                totalAmount = 144.97,
                trackingNumber = "GB982301984XP",
                deliveryEstimate = "Arriving Tomorrow",
                shippingAddress = "22 Regent Street, London, W1B 5TG, UK"
            )
        )
        dao.insertOrderItems(
            listOf(
                OrderItemEntity(
                    orderId = orderId,
                    productId = "sd-jacket-01",
                    productName = "SD Ultimate Windcheater",
                    price = 119.99,
                    quantity = 1,
                    size = "L",
                    color = "Jet Black"
                )
            )
        )
    }
}
