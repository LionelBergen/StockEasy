package dad.business.data

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Table

/** Categories **/
object CategoryTables : IntIdTable("Category") {
    val name = varchar("name", 255)
}
class CategoryTable(id: EntityID<Int>) : IntEntity(id) {
    companion object : IntEntityClass<CategoryTable>(CategoryTables)

    var name by CategoryTables.name

    var parentCategories by CategoryTable via CategoriesTable
    // var childCategories by CategoryTable via CategoriesTable //by via(Categories.parentCategory, Categories.childCategories)
    // var stores by StoreTable via StoreCategory
}
object CategoriesTable : Table("Categories") {
    val parentCategories = reference("parent_category_id", CategoryTables).primaryKey(1)
    // val childCategories = reference("child_category_id", CategoryTables).primaryKey(0)
}
