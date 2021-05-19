package dad.business.admin

import dad.business.data.DATBASE_UTIL
import dad.business.data.component.User
import dad.business.data.component.UserType
import dad.business.data.component.Variant
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
        @RequestParam(value = "categoryName", required = false) categoryName: String?,
        @RequestParam(value = "parentCategory", required = false) parentCategoryId: Int?,
        @RequestParam(value = "sortBy", required = false) sortByValue: Int?,
        @RequestParam(value = "productName", required = false) productName: String?,
        @RequestParam(value = "productCategory", required = false) productCategories: List<Int>?,
        @RequestParam(value = "vendor", required = false) vendors: List<Int>?,
        @RequestParam(value = "variantName", required = false) variantNames: List<String>?,
        @RequestParam(value = "variantPrice", required = false) variantPrices: List<Double>?,
        request: HttpServletRequest, model: Model): String {
        val session: HttpSession = request.getSession(true)
        var currentLoggedInUser: User? = session.getAttribute("user") as User?

        if (currentLoggedInUser == null) {
            return "redirect:/"
        } else if (!currentLoggedInUser.userTypes.contains(UserType.ADMIN)) {
            return "redirect:/store";
        }

        model.addAttribute("categories", DATBASE_UTIL.getAllCategories())
        model.addAttribute("vendors", DATBASE_UTIL.getAllVendors())

        if (!categoryName.isNullOrBlank() && sortByValue != null) {
            DATBASE_UTIL.insertCategory(categoryName, parentCategoryId, sortByValue)

            model.addAttribute("feedback", "Added Category!")
            return "redirect:manageProducts";
        } else if (!productName.isNullOrBlank() && productCategories!!.isNotEmpty()){
            if (!variantNames.isNullOrEmpty() && !variantPrices.isNullOrEmpty()) {
                val filteredVariantNames = variantNames.filter { e -> !e.isNullOrEmpty() };
                val filteredVariantPrices = variantPrices.filter { e -> e != null && !e.isNaN() };
                if (filteredVariantNames.size != filteredVariantPrices.size) {
                    model.addAttribute("feedback", "Invalid Variant!")
                    return "admin/manageProducts";
                }

                var variantsToAdd : List<Variant> = listOf()
                for (i in 0..(filteredVariantNames.size-1)) {
                    variantsToAdd += Variant(-1, filteredVariantNames[i], filteredVariantPrices[i])
                }

                DATBASE_UTIL.insertProduct(productName, productCategories, vendors!!, variantsToAdd)
                model.addAttribute("feedback", "Added Product!")
            } else {
                model.addAttribute("feedback", "Atleast one Variant required")
            }
        }

        return "admin/manageProducts";
    }
}