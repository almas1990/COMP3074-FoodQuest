package com.comp3074.foodquest.domain

import com.comp3074.foodquest.data.Quest
import org.junit.Assert.*
import org.junit.Test

class QuestRulesTest {
    private val taco = Quest(1, "Taco Trail", "Cafe", "Toronto", "Try a meal", listOf("Budget", "Lunch"), 50)
    private val dessert = Quest(2, "Dessert Discovery", "Bakery", "Toronto", "Try a dessert", listOf("Dessert"), 40, completed = true)

    @Test fun searchIgnoresCaseAndCombinesWithTag() {
        assertEquals(listOf(taco), QuestRules.filter(listOf(taco, dessert), "  TACO ", "budget"))
        assertTrue(QuestRules.filter(listOf(taco, dessert), "taco", "Dessert").isEmpty())
        assertEquals(listOf(dessert), QuestRules.filter(listOf(taco, dessert), "dessert", null))
    }

    @Test fun pointsOnlyIncludeCompletedQuests() {
        assertEquals(40, QuestRules.points(listOf(taco, dessert)))
        assertEquals(0, QuestRules.points(emptyList()))
    }

    @Test fun tagsRemoveEmptyAndCaseInsensitiveDuplicates() {
        assertEquals(listOf("Budget", "Lunch"), QuestRules.tags("Budget, , Lunch, budget,"))
    }

    @Test fun difficultyIsSearchable() {
        val hardQuest = taco.copy(difficulty = "Hard")
        assertEquals(listOf(hardQuest), QuestRules.filter(listOf(hardQuest, dessert), "hard", null))
    }
}
