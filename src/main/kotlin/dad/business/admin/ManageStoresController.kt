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
        @RequestParam(value = "username", required = false) username: String?,
        @RequestParam(value = "email", required = false) email: String?,
        @RequestParam(value = "fullName", required = false) fullName: String?,
        @RequestParam(value = "phoneNumber", required = false) phoneNumber: String?,
        @RequestParam(value = "userType", required = false) userTypeString: String?,
        request: HttpServletRequest, model: Model): String {
        val session: HttpSession = request.getSession(true)
        var currentLoggedInUser: User? = session.getAttribute("user") as User?
        val allUsers = DATBASE_UTIL.getAllUsers()

        if (currentLoggedInUser == null) {
            return "redirect:/"
        } else if (!currentLoggedInUser.userTypes.contains(UserType.ADMIN)) {
            return "redirect:/store";
        }

        if (storeName.isNullOrBlank()) {
            // clear the feedback
            model.addAttribute("feedback", "");
        } else {
            if (username.isNullOrBlank() || userTypeString.isNullOrBlank()) {
                model.addAttribute("feedback", "User with type is required");
            } else {
                try {
                    DATBASE_UTIL.insertStore(username, email, fullName, phoneNumber, storeName, UserType.fromValue(userTypeString))
                    model.addAttribute("feedback", "Added store successfully!");
                } catch(e : Exception) {
                    model.addAttribute("feedback", e);
                }
            }

            return "redirect:manageStores";
        }

        model.addAttribute("users", allUsers);
        model.addAttribute("userTypes", UserType.values());


        return "admin/manageStores";
    }
}