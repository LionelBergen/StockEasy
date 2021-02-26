package web.helpfiles

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach

/**
 * Classes that modify our test data and need to cleanup afterthemsleves should use extend class to do so
 */
open class ModifiesDataTest : WebTest() {
    protected var modifedTestData: Boolean = false

    private fun resetTestData() {
        Database.connect(
            "jdbc:postgresql://localhost:5432/dad_business_to_business",
            driver = "org.postgresql.Driver",
            user = "dad",
            password = "business"
        )

        // TODO: These files are duplicated from buildsrc. Find a better way of clearing the DB
        transaction {
            TransactionManager.current().exec(this::class.java.classLoader.getResource("recreateDatabase.sql").readText())
            TransactionManager.current().exec(this::class.java.classLoader.getResource("databaseLoadScript.sql").readText())
            TransactionManager.current().exec(this::class.java.classLoader.getResource("restartSequences.sql").readText())
        }
    }

    @AfterEach
    fun cleanup() {
        if (modifedTestData) {
            resetTestData()
            modifedTestData = false
        }
    }
}
