package dad.business.store

import dad.business.data.DATBASE_UTIL
import dad.business.data.component.Cart
import dad.business.data.component.CartProduct
import dad.business.data.component.User

class CartService {
    companion object {
        fun getExistingCartForUser(user: User): Cart? {
            return DATBASE_UTIL.getActiveCartForUser(user)
        }

        fun addProductToCart(user: User, cartId: Int?, productId: Int, variantId: Int, quantity: Int): Int {
            var cart: Cart?

            if (cartId == null) {
                cart = getExistingCartForUser(user)
            } else {
                cart = DATBASE_UTIL.findCartById(cartId)
            }

            if (cart == null) {
                // No cart, create one
                DATBASE_UTIL.createNewCartForUser(user)

                cart = getExistingCartForUser(user)
            }

            val existingProduct: CartProduct? = cart!!.cartProducts.find {
                it.productId == productId && it.variantId == variantId
            }

            if (existingProduct != null) {
                // product already exists, update quantity
                val newQuantity = existingProduct.quantity + quantity

                // newQuantity : Int, productId : Int, variantId : Int, cart : Cart)
                DATBASE_UTIL.updateQuantityOfCartProduct(newQuantity, productId, variantId, cart)

                return newQuantity
            } else {
                // product does not exist, add a new row
                DATBASE_UTIL.addNewProductToCart(quantity, productId, variantId, cart)
                return quantity
            }
        }
    }
}
