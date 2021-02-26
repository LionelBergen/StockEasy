package dad.business

import com.google.gson.Gson
import dad.business.data.component.*
import dad.business.store.CartService
import dad.business.store.ProductService
import dad.business.store.ProductService.Companion.getPopulatedStores
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Controller
class StoreController {
    @RequestMapping("/store")
    fun landingPage(
        request: HttpServletRequest,
        model: Model
    ): String {
        val session: HttpSession = request.getSession(true)
        var currentLoggedInUser: User? = session.getAttribute("user") as User?

        if (currentLoggedInUser == null) {
            return "redirect:/"
        }

        model.addAttribute("user", currentLoggedInUser)

        if (currentLoggedInUser.stores.isEmpty()) {
            return "store/noProducts"
        }

        // store won't be null here, as we just checked above
        val stores: List<Store> = getPopulatedStores(currentLoggedInUser.stores)!!

        model.addAttribute("store_name", currentLoggedInUser.stores.get(0).name)
        val categoriesForStores: List<StoreItem> = stores.map { it.categories }.flatten().sortedWith(compareBy(StoreItem::sortByValue, StoreItem::name))
        val gson = Gson()

        // Don't include special categories which always exist (Vendors)
        if (categoriesForStores.filter { it.type == StoreItemType.CATEGORY }.isEmpty()) {
            return "store/noProducts"
        }

        val userCart = CartService.getExistingCartForUser(currentLoggedInUser)
        val cartSize = if (userCart == null) 0 else userCart.cartProducts.size

        model.addAttribute("categories", gson.toJson(categoriesForStores))
        model.addAttribute("cart", gson.toJson(userCart))
        model.addAttribute("cartSize", cartSize)

        session.setAttribute("cartId", userCart?.id)

        return "store/productPage"
    }

    @RequestMapping("/getProducts")
    @ResponseBody
    fun getProducts(@RequestParam(value = "categoryId") categoryId: Int): String {
        val storeProducts: List<StoreProduct> = ProductService.searchProducts(categoryId).sortedBy {
            it.productName
        }
        return "{\"result\": ${Gson().toJson(storeProducts)}}"
    }

    @RequestMapping("/getProductsByVendor")
    @ResponseBody
    fun getProductsByVendor(@RequestParam(value = "vendorId") vendorId: Int): String {
        val storeProducts: List<StoreProduct> = ProductService.searchProductsByVendor(vendorId).sortedBy {
            it.productName
        }
        return "{\"result\": ${Gson().toJson(storeProducts)}}"
    }

    @RequestMapping("/searchProducts")
    @ResponseBody
    fun searchProducts(@RequestParam(value = "searchString") searchString: String): String {
        return "{\"result\": ${Gson().toJson(ProductService.searchProducts(searchString))}}"
    }

    @RequestMapping("/addProductToCart")
    @ResponseBody
    fun addProductToCart(
        @RequestParam productId: Int,
        @RequestParam variantId: Int,
        @RequestParam quantity: Int,
        request: HttpServletRequest
    ): String {
        val session: HttpSession = request.getSession(false)
        val cartId = session.getAttribute("cartId") as Int?
        val user = session.getAttribute("user") as User

        // add the product to the database
        val productAmountsChange = quantity == CartService.addProductToCart(user, cartId, productId, variantId, quantity)

        return "{\"result\": " +
                "{ \"productAmountChanged\": ${Gson().toJson(productAmountsChange)} } }"
    }
}
