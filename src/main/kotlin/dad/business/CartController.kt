package dad.business

import dad.business.data.DATBASE_UTIL
import dad.business.data.component.Cart
import dad.business.data.component.CartStatus
import dad.business.data.component.User
import dad.business.service.email.EmailService
import dad.business.store.CartService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Controller
class CartController {
    @Autowired
    private lateinit var emailService: EmailService

    @RequestMapping("/submitCartFinal")
    fun submitCartFinal(request: HttpServletRequest, model: Model): String {
        val session: HttpSession = request.getSession(true)
        val cartId: Int? = session.getAttribute("cartId") as Int?

        if (cartId == null) {
            return "redirect:/"
        }

        val userCart: Cart? = DATBASE_UTIL.findCartById(cartId)

        if (userCart == null || userCart.cartProducts.size == 0) {
            return "redirect:/"
        }

        // send email given cart
        emailService.sendAnEmail(userCart)

        // delete the cart
        DATBASE_UTIL.deleteUserCart(cartId, CartStatus.PROCESSED)
        session.setAttribute("cartId", null)

        return "redirect:/"
    }

    @RequestMapping("/cart")
    fun goToCartPage(request: HttpServletRequest, model: Model): String {
        val session: HttpSession = request.getSession(true)
        var cartId: Int? = session.getAttribute("cartId") as Int?
        var currentLoggedInUser: User? = session.getAttribute("user") as User?

        if (currentLoggedInUser == null) {
            return "redirect:/"
        }

        if (cartId == null) {
            cartId = CartService.getExistingCartForUser(currentLoggedInUser)?.id
            session.setAttribute("cartId", cartId)
        }

        if (cartId == null) {
            return "store/emptyCart"
        }

        val userCart: Cart? = DATBASE_UTIL.findCartById(cartId)

        if (userCart == null || userCart.cartProducts.isEmpty()) {
            return "store/emptyCart"
        }

        model.addAttribute("cart_products", userCart.cartProducts)
        session.setAttribute("cartId", userCart.id)

        return "store/cart"
    }

    @RequestMapping("/deleteCart")
    fun deleteCart(request: HttpServletRequest, model: Model): String {
        val session: HttpSession = request.getSession(true)
        val cartId: Int? = session.getAttribute("cartId") as Int?

        if (cartId != null) {
            // archive the cart
            DATBASE_UTIL.deleteUserCart(cartId, CartStatus.DELETED)

            session.setAttribute("cartId", null)
        }

        return "redirect:/"
    }

    @RequestMapping("/submitCart")
    fun submitCart(request: HttpServletRequest, model: Model): String {
        val session: HttpSession = request.getSession(true)
        val cartId: Int? = session.getAttribute("cartId") as Int?

        if (cartId == null) {
            return "redirect:/"
        }

        val userCart: Cart? = DATBASE_UTIL.findCartById(cartId)

        if (userCart == null || userCart.cartProducts.size == 0) {
            return "redirect:/"
        }

        val result = emailService.getEmailPreviewObjectFromCart(userCart)

        model.addAttribute("email_preview", result)

        return "services/emailPreview"
    }

    @RequestMapping("/changeQuantity")
    @ResponseBody
    fun changeProductQuantity(
        request: HttpServletRequest,
        @RequestParam quantity: Int,
        @RequestParam productId: Int,
        @RequestParam variantId: Int,
        @RequestParam vendorId: Int
    ): String {
        val session: HttpSession = request.getSession(true)
        val cartId: Int = session.getAttribute("cartId") as Int
        val userCart: Cart = DATBASE_UTIL.findCartById(cartId)!!

        DATBASE_UTIL.updateQuantityOfCartProduct(quantity, productId, variantId, userCart)

        return "success"
    }

    @RequestMapping("/removeProduct")
    @ResponseBody
    fun removeProductFromCart(
        request: HttpServletRequest,
        model: Model,
        @RequestParam productId: Int,
        @RequestParam variantId: Int,
        @RequestParam vendorId: Int
    ): String {
        val session: HttpSession = request.getSession(true)
        val cartId: Int = session.getAttribute("cartId") as Int

        println("deleting...")
        DATBASE_UTIL.deleteProductFromCart(productId, variantId, cartId)
        println("deleted...")

        val userCart: Cart = DATBASE_UTIL.findCartById(cartId)!!
        model.addAttribute("cart_products", userCart.cartProducts)

        return "success"
    }
}
