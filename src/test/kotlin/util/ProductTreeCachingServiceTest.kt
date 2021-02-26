package util

import dad.business.data.component.StoreItem
import dad.business.data.component.StoreItemType
import dad.business.util.ProductTreeCachingService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail

class ProductTreeCachingServiceTest {
    private val treeService: ProductTreeCachingService = ProductTreeCachingService()

    @Test
    fun testAddDuplicate() {
        val topLevelCategory: StoreItem = createTestCategory(1, "test parent")

        treeService.addCategoryToMap(topLevelCategory, null)
        try {
            treeService.addCategoryToMap(topLevelCategory, null)
            fail("We expected an exception!")
        } catch (e: Exception) {
            assertTrue(e.localizedMessage.contains("Category already exists: "))
        }
    }

    @Test
    fun testSimpleAdd() {
        val testCategory: StoreItem = createTestCategory(1, "test parent")
        treeService.addCategoryToMap(testCategory, null)

        val result: Set<StoreItem> = treeService.getStoreTree()
        assertEquals(1, result.size)
        assertEquals(Integer(1), result.iterator().next().id)
    }

    @Test
    fun testLargeTreeCategories() {
        // parent top level categories
        val category1: StoreItem = createTestCategory(111, "parent_1")
        val category2: StoreItem = createTestCategory(1, "Alcohol")
        val category3: StoreItem = createTestCategory(112, "parent_2")
        treeService.addCategoryToMap(category1, null)
        treeService.addCategoryToMap(category2, null)
        treeService.addCategoryToMap(category3, null)
        assertEquals(3, treeService.getStoreTree().size)

        // categories which have children and belong to the parents above
        // Don't add in order, we want to ensure the order entered does not matter, the tree service should handle this
        treeService.addCategoryToMap(createTestCategory(110, "child_two_parents"), createTestCategory(112, "parent_2"))
        treeService.addCategoryToMap(createTestCategory(110, "child_two_parents"), createTestCategory(111, "parent_1"))
        assertEquals(3, treeService.getStoreTree().size); // Tree should move children to parents
        treeService.addCategoryToMap(createTestCategory(11, "Miller 3"), createTestCategory(10, "Miller 2"))
        treeService.addCategoryToMap(createTestCategory(10, "Miller 2"), createTestCategory(9, "Miller 1"))
        assertEquals(4, treeService.getStoreTree().size); // Tree should move child to new parent
        treeService.addCategoryToMap(createTestCategory(9, "Miller 1"), createTestCategory(8, "Miller"))
        treeService.addCategoryToMap(createTestCategory(8, "Miller"), createTestCategory(6, "Beer"))
        treeService.addCategoryToMap(createTestCategory(13, "BeerProducts"), createTestCategory(6, "Beer"))
        treeService.addCategoryToMap(createTestCategory(7, "Wine"), createTestCategory(1, "Alcohol"))
        treeService.addCategoryToMap(createTestCategory(6, "Beer"), createTestCategory(1, "Alcohol"))

        val result: Set<StoreItem> = treeService.getStoreTree()
        assertEquals(3, result.size)

        // assert Alcohol
        val alcoholResult = result.find { it.id == 1 }
        assertEquals(2, alcoholResult!!.children.size)

        // assert parent_1
        val parent1Result = result.find { it.id == 111 }
        assertEquals(1, parent1Result!!.children.size)

        // assert parent_2
        val parent2Result = result.find { it.id == 112 }
        assertEquals(1, parent2Result!!.children.size)
    }

    private fun createTestCategory(int: Int, name: String): StoreItem {
        return StoreItem(int, 100, StoreItemType.valueOf("CATEGORY"), name, listOf(), null)
    }
}
