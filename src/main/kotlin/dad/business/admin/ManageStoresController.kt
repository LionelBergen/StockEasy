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
class ManageStoresController {
    @RequestMapping("/admin/manageStores")
    fun adminLandingPage(
        @RequestParam(value = "storeName", required = false) storeName: String?,
        request: HttpServletRequest, model: Model): String {
        val session: HttpSession = request.getSession(true)
        var currentLoggedInUser: User? = session.getAttribute("user") as User?
        val allUsers = DATBASE_UTIL.getAllUsers()
        println(allUsers)

        if (currentLoggedInUser == null) {
            return "redirect:/"
        } else if (!currentLoggedInUser.userTypes.contains(UserType.ADMIN)) {
            return "redirect:/store";
        }

        if (storeName != null) {
            // DATBASE_UTIL.
        } else {
            // clear the feedback
            model.addAttribute("feedback", "");
        }

        model.addAttribute("users", allUsers);


        return "admin/manageStores";
    }
}