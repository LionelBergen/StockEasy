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
class ManageVendorsController {
    @RequestMapping("/admin/manageVendors")
    fun adminLandingPage(
        @RequestParam(value = "name", required = false) vendorName: String?,
        @RequestParam(value = "email", required = false) email: String?,
        request: HttpServletRequest, model: Model): String {
        val session: HttpSession = request.getSession(true)
        var currentLoggedInUser: User? = session.getAttribute("user") as User?
        val allVendors = DATBASE_UTIL.getAllVendors()

        if (currentLoggedInUser == null) {
            return "redirect:/"
        } else if (!currentLoggedInUser.userTypes.contains(UserType.ADMIN)) {
            return "redirect:/store";
        }

        if (vendorName.isNullOrBlank() && email.isNullOrBlank()) {
            // clear the feedback
            model.addAttribute("feedback", "");
        } else {
            return "redirect:manageVendors";
        }

        model.addAttribute("vendors", allVendors);

        return "admin/manageVendors";
    }
}