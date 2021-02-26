package dad.business.util

import dad.business.data.component.StoreItem

class ProductTreeCachingService {
    private var storeTree: Set<StoreItem> = HashSet()

    /**
     * Add a category and/or the parent depending on the state of the tree.
     * Adds the category to the correct placement depending on the parent
     */
    fun addCategoryToMap(category: StoreItem, parentCategory: StoreItem?) {
        if (parentCategory == null) {
            // We want to throw an error, not replace the category
            if (treeContainsCategory(category)) {
                throw Exception("Category already exists: $category")
            }

            addCategoryTopLevel(category)
        } else if (treeContainsCategory(parentCategory)) {
            if (treeContainsCategory(category)) {
                // this catergory exists, we need to move it to the existing parent
                moveCategoryToNewParent(getExistingCategoryFromTree(category), getExistingCategoryFromTree(parentCategory))
            } else {
                // category does not exist, add it as a child to the parent
                getExistingCategoryFromTree(parentCategory).children += category
            }
        } else {
            if (treeContainsCategory(category)) {
                // parent does not exist but this category does, so create the parent and move this category to its children
                addCategoryTopLevel(parentCategory)
                moveCategoryToNewParent(getExistingCategoryFromTree(category), parentCategory)
            } else {
                // parent does not exist. add it
                addCategoryTopLevel(parentCategory)
                parentCategory.children += category
            }
        }
    }

    fun getStoreTree(): Set<StoreItem> {
        return storeTree
    }

    private fun moveCategoryToNewParent(existingCategoryToMove: StoreItem, existingParentCategory: StoreItem) {
        storeTree -= existingCategoryToMove

        existingParentCategory.children += existingCategoryToMove
    }

    private fun getExistingCategoryFromTree(storeItem: StoreItem): StoreItem {
        return storeTree.find {
             it.id == storeItem.id
        }!!
    }

    private fun addCategoryTopLevel(category: StoreItem) {
        storeTree += category
    }

    private fun treeContainsCategory(storeItem: StoreItem): Boolean {
        return storeTree.any {
            e -> e.id == storeItem.id && e.type == storeItem.type
        }
    }
}
