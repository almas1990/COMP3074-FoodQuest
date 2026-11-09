package com.comp3074.foodquest.data

import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

class QuestRepositoryTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private lateinit var repository: QuestRepository

    @Before fun setup() {
        context.deleteDatabase("foodquest-test.db")
        repository = QuestRepository(FoodQuestDatabase.open(context, "foodquest-test.db"))
    }

    @After fun cleanup() { repository.close(); context.deleteDatabase("foodquest-test.db") }

    @Test fun questSurvivesReopenAndSupportsEditRatingCompletionAndDelete() {
        val id = repository.save(Quest(name = "Lunch Hunt", place = "Cafe", address = "Toronto",
            task = "Find lunch", tags = listOf("Budget", "Comma, tag"), points = 50))
        repository.complete(id)
        repository.complete(id)
        repository.rate(id, 5)
        repository.close()
        repository = QuestRepository(FoodQuestDatabase.open(context, "foodquest-test.db"))
        val saved = repository.quests().single()
        assertEquals(id, saved.id)
        assertTrue(saved.completed)
        assertEquals(5, saved.rating)
        assertEquals(listOf("Budget", "Comma, tag"), saved.tags)
        assertEquals(50, repository.quests().filter { it.completed }.sumOf { it.points })
        repository.save(saved.copy(name = "Edited lunch"))
        assertEquals("Edited lunch", repository.quests().single().name)
        repository.delete(id)
        assertTrue(repository.quests().isEmpty())
    }

    @Test fun teamEditsSurviveReopen() {
        assertEquals(3, repository.members().size)
        val member = repository.members().first()
        repository.saveMember(member.copy(email = "student@example.com", phone = "4165550100"))
        repository.close()
        repository = QuestRepository(FoodQuestDatabase.open(context, "foodquest-test.db"))
        assertEquals("student@example.com", repository.members().first().email)
    }

    @Test(expected = IllegalArgumentException::class)
    fun invalidPointsAreRejected() {
        repository.save(Quest(name = "Lunch", place = "Cafe", address = "Toronto", task = "Eat", tags = emptyList(), points = -1))
    }

    @Test fun studentIdCorrectionPreservesExistingQuestsAndContacts() {
        val member = repository.members().first { it.name == "Almas Sultana" }
        repository.saveMember(member.copy(email = "student@example.com"))
        val questId = repository.save(Quest(name = "Keep this quest", place = "Cafe", address = "Toronto",
            task = "Find lunch", tags = listOf("Lunch"), points = 50))
        repository.close()

        // Version 1 has the same tables, but seeded the incorrect registration ID.
        SQLiteDatabase.openDatabase(context.getDatabasePath("foodquest-test.db").path,
            null, SQLiteDatabase.OPEN_READWRITE).use { oldDatabase ->
            oldDatabase.execSQL("UPDATE members SET studentId = ? WHERE name = ?",
                arrayOf("101564373", "Almas Sultana"))
            oldDatabase.version = 1
        }

        repository = QuestRepository(FoodQuestDatabase.open(context, "foodquest-test.db"))
        val corrected = repository.members().first { it.name == "Almas Sultana" }
        assertEquals("101568934", corrected.studentId)
        assertEquals("student@example.com", corrected.email)
        assertEquals(questId, repository.quests().single().id)
    }
}
