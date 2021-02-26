package dad.business.service.email

import dad.business.data.component.Cart

/* TODO: Seperate sending an email from everything else.
* At the same time, controllers/services shouldn't care about the process for converting a Cart to an Email message
*/

/**
 *
 */
class EmailServiceMock : EmailService {
    override fun sendAnEmail(cart: Cart) {
        println("EmailServiceMock : sendAndEmail invoked.")
    }

    override fun getEmailPreviewObjectFromCart(cart: Cart): List<EmailMessage> {
        return EmailServiceImpl().getEmailPreviewObjectFromCart(cart)
    }
}
