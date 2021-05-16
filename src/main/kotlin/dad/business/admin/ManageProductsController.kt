package dad.business.admin

import dad.business.data.DATBASE_UTIL
import dad.business.data.component.User
import dad.business.data.component.UserType
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam

@Controller
class ManageProductsController {
    @RequestMapping("/admin/manageProducts")
    fun adminLandingPage(
        @RequestParam(value = "name", required = false) vendorName: String?,
        @RequestParam(value = "email", required = false) email: String?,
        request: HttpServletRequest, model: Model): String {
        val session: HttpSession = request.getSession(true)
        var currentLoggedInUser: User? = session.getAttribute("user") as User?

        if (currentLoggedInUser == null) {
            return "redirect:/"
        } else if (!currentLoggedInUser.userTypes.contains(UserType.ADMIN)) {
            return "redirect:/store";
        }

        if (vendorName.isNullOrBlank() && email.isNullOrBlank()) {
            // clear the feedback
            model.addAttribute("feedback", "")
        } else {
            if (vendorName.isNullOrBlank() || email.isNullOrBlank()) {
                model.addAttribute("feedback", "Vendorname and email required")
            } else {
                try {
                    DATBASE_UTIL.insertVendor(vendorName, email)
                    model.addAttribute("feedback", "Added vendor!")
                } catch (e : Exception) {
                    model.addAttribute("feedback", e)
                }
            }

            return "redirect:manageProducts";
        }

        model.addAttribute("categories", DATBASE_UTIL.getAllCategories())
        return "admin/manageProducts";
    }
}