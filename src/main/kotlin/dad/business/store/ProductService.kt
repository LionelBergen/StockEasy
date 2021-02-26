package dad.business.store

import dad.business.data.DATBASE_UTIL
import dad.business.data.component.Store
import dad.business.data.component.StoreItem
import dad.business.data.component.StoreProduct
import dad.business.data.component.StoreWithCategoryIds
import dad.business.util.ProductTreeCachingService

class ProductService {
    companion object {
        private val storeItemTree: Set<StoreItem>

        fun getPopulatedStores(stores: List<StoreWithCategoryIds>): List<Store>? {
            val populatedStores = ArrayList<Store>()

            stores.forEach {
                val filteredCategories = storeItemTree.filter { category ->
                    it.categories.contains(category.id) || category.name == "Vendors"
                }

                populatedStores += Store(it.name, filteredCategories)
            }

            return populatedStores
        }

        fun searchProducts(categoryId: Int): Set<StoreProduct> {
            val storeProducts = DATBASE_UTIL.getProducts(categoryId)

            return storeProducts
        }

        fun searchProductsByVendor(vendorId: Int): Set<StoreProduct> {
            val storeProducts = DATBASE_UTIL.getProductsByVendor(vendorId)

            return storeProducts
        }

        // TODO: need to also filter by store
        fun searchProducts(searchKeyWord: String): Set<StoreProduct> {
            val storeProducts = DATBASE_UTIL.getProducts(searchKeyWord)

            return storeProducts
        }

        init {
            // get all categories with their parents
            val categoriesWithParents: List<StoreItem> = DATBASE_UTIL.getAllStoreItems()

            // transform into a proper tree
            storeItemTree = transformCategoriesIntoTree(categoriesWithParents)
        }
    }
}

private fun transformCategoriesIntoTree(categories: List<StoreItem>): Set<StoreItem> {
    val productTreeCachingservice = ProductTreeCachingService()

    categories.forEach {
        productTreeCachingservice.addCategoryToMap(it, it.parentCategory)
    }

    return productTreeCachingservice.getStoreTree()
}
