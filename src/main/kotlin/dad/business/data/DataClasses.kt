package dad.business.data

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

/** UserTable **/
object UserTables : IntIdTable("User") {
    val username = varchar("username", 255)
    val password = varchar("password", 255)
    val email = varchar("email", 255).nullable()
    val fullName = varchar("full_name", 255).nullable()
    val phoneNumber = varchar("phone_number", 255).nullable()
}
class UserTable(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserTable>(UserTables)

    var username by UserTables.username
    var password by UserTables.password
    var email by UserTables.email
    var fullName by UserTables.fullName
    var phoneNumber by UserTables.phoneNumber
    var userTypes by UserTypeTable via UserUserTypeTable
    var stores by StoreTable via UserStoreTable
}

/**UserType **/
class UserTypeTable(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<UserTypeTable>(UserTypeTables)

    var userType by UserTypeTables.userType
}
object UserTypeTables : IntIdTable("User_Type") {
    val userType = varchar("user_type", 255)
}
object UserUserTypeTable : Table(name = "user_user_type") {
    val user = reference("user_id", UserTables).primaryKey(0)
    val type = reference("user_type_id", UserTypeTables).primaryKey(1)
}

/** Store **/
object StoreTables : IntIdTable("Store") {
    val name = varchar("name", 255)
}
class StoreTable(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<StoreTable>(StoreTables)

    var name by StoreTables.name
    var categories by CategoryTable via StoreCategory
}
object UserStoreTable : Table("User_Store") {
    val user = reference("user_id", UserTables).primaryKey(0)
    val store = reference("store_id", StoreTables).primaryKey(1)
}

object StoreCategory : Table("store_category") {
    val store = reference("store_id", StoreTables).primaryKey(0)
    val category = reference("category_id", CategoryTables).primaryKey(1)
}
