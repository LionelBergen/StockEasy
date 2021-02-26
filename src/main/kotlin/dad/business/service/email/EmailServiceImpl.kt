package dad.business.service.email

import dad.business.data.component.Cart
import dad.business.data.component.CartProduct
import dad.business.util.SystemVariables
import net.sargue.mailgun.Configuration
import net.sargue.mailgun.Mail
import net.sargue.mailgun.Response

data class EmailMessage(val recipient: String, val subject: String, val userFullName: String?, val userPhoneNumber: String?, val userEmail: String?, val items: List<ProductEmailItem>) {
    private val EMAIL_LINE_BREAK = "<br />"

    fun getBody(): String {
        var resultBody = "<h1>Order From: $userFullName</h1><h2>contact: $userPhoneNumber $userEmail</h2><h2>Orders</h2>"
        resultBody += "<table><thead><tr><th>Item</th><th>Quantity</th></tr></thead><tbody>"

        for (item in items) {
            resultBody += "<tr><td>${item.itemDescription}</td>" +
                "<td>${item.quantity}</td>" +
                "</tr>"
        }

        resultBody += "</tbody></table>"

        return resultBody
    }
}
data class ProductEmailItem(val quantity: String, val itemDescription: String)

class EmailServiceImpl : EmailService {
    private val configuration: Configuration = Configuration()
        .domain(SystemVariables.mailgunDomain)
        .apiKey(SystemVariables.mailgunApiKey)
        .from(SystemVariables.mailgunFromName, SystemVariables.mailgunFromEmail)

    /**
     * Sends emails given cart details
     */
    override fun sendAnEmail(cart: Cart) {
        val emailMessages = getEmailMessagesFromCart(cart)

        for (emailMessage in emailMessages) {
            val result = sendEmail(emailMessage)

            println("Sent message got response: ${result?.responseCode()} with code ${result?.responseMessage()}")
        }
    }

    override fun getEmailPreviewObjectFromCart(cart: Cart): List<EmailMessage> {
        return getEmailMessagesFromCart(cart)
    }

    private fun sendEmail(message: EmailMessage): Response? {
        println("Sent email to: ${message.recipient}, subject: ${message.subject}")

        return Mail.using(configuration)
            .to(message.recipient)
            .subject(message.subject)
            .html(createHtmlEmail(message.getBody()))
            .build()
            .send()
    }

    private fun createHtmlEmail(emailBody: String): String {
        val result = getHeaderHtml() + "<div>$emailBody</div>" + getFooterHtml()
        println(result)
        return result
    }

    private fun getHeaderHtml(): String {
        return "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\"><html xmlns=\"http://www.w3.org/1999/xhtml\"><head><meta name=\"viewport\" content=\"width=device-width\" /><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" /><title>Automated Order</title><link href=\"styles.css\" media=\"all\" rel=\"stylesheet\" type=\"text/css\" /></head><body itemscope itemtype=\"http://schema.org/EmailMessage\">"
    }

    private fun getFooterHtml(): String {
        return "</body></html>"
    }

    /**
     * Given a cart, product some email objects used to either preview, or send an email
     */
    private fun getEmailMessagesFromCart(cart: Cart): List<EmailMessage> {
        var results: List<EmailMessage> = ArrayList<EmailMessage>()

        // get a map of results based on Vendor email address
        val emailMap: Map<String, List<CartProduct>> = cart.cartProducts.groupBy { it.vendorEmail }

        // for each vendorEmail, create an Email object to send to that email address
        for (emailItem in emailMap) {
            var productItems: List<ProductEmailItem> = ArrayList<ProductEmailItem>()
            val vendorEmail = emailItem.key
            val subject = "Automated Order From ${cart.username}"

            for (cartProduct in emailItem.value) {
                val productDescription = "${cartProduct.productName} ${cartProduct.variantName}"
                productItems += ProductEmailItem(
                    cartProduct.quantity.toString(),
                    productDescription
                )
            }

            results += EmailMessage(
                vendorEmail,
                subject,
                cart.userFullName,
                cart.phoneNumber,
                cart.userEmail,
                productItems
            )
        }

        return results
    }
}
