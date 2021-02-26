package dad.business.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

object Cities : Table() {
    val id = integer("id").autoIncrement() // Column<Int>
    val name = varchar("name", 50) // Column<String>
}

open class DbLoadTestDataLocal : DefaultTask() {
    init {
        group = "dad.business"
        description = "Loads a database full of test data"
    }

    @TaskAction
    fun run() {
        createDatabaseAndRunScript("databaseLoadScript.sql");
    }
}

open class DbLoadKomodoLoco : DefaultTask() {
    init {
        group = "dad.business"
        description = "Loads data containing Komodo Loco store"
    }

    @TaskAction
    fun run() {
        createDatabaseAndRunScript("KomodoLoco.sql");
    }
}

private fun createDatabaseAndRunScript(sqlFileName : String) {
    println("Database kotlin script running.. ")
    Database.connect("jdbc:postgresql://localhost:5432/dad_business_to_business", driver = "org.postgresql.Driver", user = "dad", password = "business")

    // TODO: This will delete and recreate the database and user rather than dropping all tables
    transaction {
        TransactionManager.current().exec(this::class.java.classLoader.getResource("recreateDatabase.sql").readText())
        TransactionManager.current().exec(this::class.java.classLoader.getResource(sqlFileName).readText())
        TransactionManager.current().exec(this::class.java.classLoader.getResource("restartSequences.sql").readText())
    }

    println("Database kotlin script finished.")
}