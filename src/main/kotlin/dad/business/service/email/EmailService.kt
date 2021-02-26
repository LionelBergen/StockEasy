package dad.business.service.email

import dad.business.data.component.Cart

interface EmailService {
    fun sendAnEmail(cart: Cart)
    fun getEmailPreviewObjectFromCart(cart: Cart): List<EmailMessage>
}
