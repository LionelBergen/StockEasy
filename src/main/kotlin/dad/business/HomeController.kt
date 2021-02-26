package dad.business

import dad.business.data.component.User
import dad.business.data.component.UserType
import dad.business.user.IpAddressService
import dad.business.user.UserService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpSession

@Controller
class RestController {
    @RequestMapping("/")
    fun landingPage(
        @RequestParam(value = "username", required = false) username: String?,
        @RequestParam(value = "password", required = false) password: String?,
        request: HttpServletRequest,
        model: Model
    ): String {
        println("Username: $username password: $password")
        val session: HttpSession = request.getSession(true)
        val ipAddress = IpAddressService.getIpAddressFromRequest(request)

        var currentLoggedInUser: User? = session.getAttribute("user") as User?

        if (username != null && currentLoggedInUser == null) {
            val user = UserService.getUser(username, password)

            // invalid username or password
            if (user == null) {
                model.addAttribute("error", "Invalid credentials")
                UserService.addLoginAttempt(username, password, ipAddress, false)
            } else {
                currentLoggedInUser = user
                session.setAttribute("user", user)
                UserService.addLoginAttempt(username, password, ipAddress, true)
            }
        }

        if (currentLoggedInUser != null) {
            model.addAttribute("username", currentLoggedInUser.username)
            model.addAttribute("user", currentLoggedInUser)

            if (currentLoggedInUser.userTypes.contains(UserType.ADMIN)) {
                return "redirect:admin"
            } else {
                return "redirect:store"
            }
        }

        return "login"
    }

    @RequestMapping("/logout")
    fun logout(request: HttpServletRequest): String {
        println("logged out...")
        request.getSession().invalidate()
        return "redirect:/"
    }
}
