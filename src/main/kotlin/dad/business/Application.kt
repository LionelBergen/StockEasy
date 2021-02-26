package dad.business

import dad.business.service.email.EmailService
import dad.business.service.email.EmailServiceImpl
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@SpringBootApplication
class Application

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

@Configuration
class BeanConfiguration {
    @Bean
    fun emailService(): EmailService {
        return EmailServiceImpl()
    }
}
