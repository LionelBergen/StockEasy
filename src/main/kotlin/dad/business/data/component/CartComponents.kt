package dad.business.data.component

import java.text.NumberFormat
import java.util.*

data class Cart(val id: Int, val status: CartStatus, val cartUser: CartUser, var cartProducts: List<CartProduct>) {
    constructor(id: Int, status: CartStatus, userId: Int, username: String, userEmail: String?, userFullName: String?, phoneNumber: String?, cartProducts: List<CartProduct>) :
        this(id, status, CartUser(userId, username, userEmail, userFullName, phoneNumber), cartProducts)

    val userFullName = cartUser.fullName
    val phoneNumber = cartUser.phoneNumber
    val userEmail = cartUser.email
    val username = cartUser.username
    val userId = cartUser.id
}
data class CartUser(val id: Int, val username: String, val email: String?, val fullName: String?, val phoneNumber: String?)
data class CartProduct(val price: Double, val quantity: Int, val productName: String, val productId: Int, val variantName: String, val variantId: Int, val vendorName: String, val vendorEmail: String, val vendorId: Int) {
    val priceDisplay = NumberFormat.getCurrencyInstance(Locale.CANADA).format(price)
}

enum class CartStatus(val id: Int, val type: String) {
    ACTIVE(1, "Active"),
    DELETED(2, "Deketed"),
    PROCESSED(3, "Processed");
}
