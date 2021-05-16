package dad.business.data.component

// These objects are used in an HTTP session, so they will not contain a Tree. Just basic info
data class User(val id: Int, var username: String, var password: String, var email: String?, var fullName: String?, var phoneNumber: String?, var userTypes: List<UserType>, var stores: List<StoreWithCategoryIds>)
data class StoreWithCategoryIds(val name: String, var categories: List<Int>)

// This is a large object different from the one we use for the user. It's fully populated with categories & products
data class Store(val name: String, var categories: List<StoreItem>)
data class StoreItem(val id: Int, val sortByValue: Int?, val type: StoreItemType, val name: String, var children: List<StoreItem>, @Transient val parentCategory: StoreItem?) {
    override fun hashCode(): Int {
        return (id.toInt() * 31 + name.hashCode()) * 31
    }

    override fun toString(): String {
        return "id: $id, type: $type, name: $name "
    }

    override fun equals(other: Any?): Boolean {
        return other != null && other.javaClass == this.javaClass && other.hashCode() == this.hashCode()
    }

    fun deepCopy(id: Int = this.id, sortByValue: Int? = this.sortByValue, type: StoreItemType = this.type, name: String = this.name, children: List<StoreItem> = this.children, parentCategory: StoreItem? = this.parentCategory): StoreItem {
        var childrenCopy: List<StoreItem> = ArrayList<StoreItem>()

        for (child in children) {
            childrenCopy += child.deepCopy()
        }

        return StoreItem(id, sortByValue, type, name, children, parentCategory?.deepCopy())
    }
}

data class Category(val id: Int, val name: String)
data class Variant(val id: Int, val name: String, val price: Double)
data class Vendor(val id: Int, val name : String, val email: String)
data class StoreProduct(val id: Int, val productName: String, var categories: Set<Category>, var variants: Set<Variant>, val vendorEmail: String)

enum class StoreItemType(val type: String) {
    CATEGORY("Category"),
    PRODUCT("Product"),
    VENDORS("Vendors"),
    VENDOR("Vendor");
}

enum class UserType(val code : Int, val type : String) {
    ADMIN(1, "admin"),
    STORE_OWNER(2, "store_owner"),
    STORE_REP(3, "store_rep");

    companion object {
        fun fromValue(value: String) : UserType {
            val foundValue = values().find {
                it.type.toLowerCase() == value.trim().toLowerCase()
            };

            if (foundValue == null) {
                throw Exception("Unknown UserType: $value")
            }

            return foundValue
        }
    }
}
