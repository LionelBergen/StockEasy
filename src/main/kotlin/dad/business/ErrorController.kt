package dad.business

import javax.servlet.RequestDispatcher
import javax.servlet.http.HttpServletRequest
import org.springframework.boot.web.servlet.error.ErrorController
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping

@Controller
class MyErrorController : ErrorController {
    @RequestMapping("/error")
    fun handleError(request: HttpServletRequest): String {
        val status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE)

        if (status != null && status == HttpStatus.NOT_FOUND.value()) {
            return "404"
        }

        // TODO: should log these errors
        return "error"
    }

    override fun getErrorPath(): String {
        return "/error"
    }
}
