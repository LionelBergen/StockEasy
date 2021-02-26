package dad.business.util

// TODO: setup spring properties properly
class SystemVariables {
    companion object {
        val dbName: String
        val dbPort: String
        val dbHost: String
        val dbPassword: String
        val dbUsername: String
        val dbType: String = "postgresql"
        val dbURL: String

        val mailgunDomain: String = System.getenv("MAILGUN_DOMAIN")
        val mailgunApiKey: String = System.getenv("MAILGUN_KEY")
        val mailgunFromName: String = System.getenv("MAILGUN_FROM_NAME")
        val mailgunFromEmail: String = System.getenv("MAILGUN_FROM_EMAIL")

        init {
            var dbFullURL: String = "${System.getenv("DATABASE_URL")}"

            val portWithDbName = dbFullURL.substringAfterLast(":")
            dbName = portWithDbName.substringAfterLast("/")
            dbPort = portWithDbName.substringBeforeLast("/")
            dbFullURL = dbFullURL.substringBeforeLast(":")
            dbHost = dbFullURL.substringAfterLast("@")
            dbFullURL = dbFullURL.substringBeforeLast("@")
            dbPassword = dbFullURL.substringAfterLast(":")
            dbFullURL = dbFullURL.substringBeforeLast(":")
            dbUsername = dbFullURL.substringAfterLast("://")

            dbURL = "jdbc:$dbType://$dbHost:$dbPort/$dbName"
        }
    }
}
