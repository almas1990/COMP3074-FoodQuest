package com.comp3074.foodquest

import androidx.test.platform.app.InstrumentationRegistry
import com.comp3074.foodquest.data.FoodQuestDatabase
import com.comp3074.foodquest.data.QuestRepository
import com.comp3074.foodquest.data.TeamMember
import com.comp3074.foodquest.domain.MemberValidation
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MemberValidationTest {
    private val context get() = InstrumentationRegistry.getInstrumentation().targetContext
    private val databaseName = "member-validation-test.db"
    private lateinit var repository: QuestRepository

    @Before fun setup() {
        context.deleteDatabase(databaseName)
        repository = QuestRepository(FoodQuestDatabase.open(context, databaseName))
    }

    @After fun cleanup() { repository.close(); context.deleteDatabase(databaseName) }

    @Test fun trimsContactsAndPreservesEditedMemberAcrossReopen() {
        val before = repository.members()
        val member = before.first()
        repository.saveMember(member.copy(name = "  ${member.name}  ", email = " student@example.com ", phone = " +1 416-555-0100 "))
        repository.close()
        repository = QuestRepository(FoodQuestDatabase.open(context, databaseName))
        val saved = repository.members().single { it.id == member.id }
        assertEquals(member.name, saved.name)
        assertEquals("student@example.com", saved.email)
        assertEquals("+1 416-555-0100", saved.phone)
        assertEquals(before.filter { it.id != member.id }, repository.members().filter { it.id != member.id })
    }

    @Test fun rejectsInvalidDataAtRepositoryBoundaryWithoutAddingRecords() {
        val count = repository.members().size
        listOf(
            TeamMember(name = "   ", studentId = ""),
            TeamMember(name = "Example", studentId = "", email = "not-an-email"),
            TeamMember(name = "Example", studentId = "", phone = "letters-only")
        ).forEach { invalid ->
            try { repository.saveMember(invalid); fail("Invalid member was saved") }
            catch (_: IllegalArgumentException) { /* Expected validation failure. */ }
        }
        assertEquals(count, repository.members().size)
    }

    @Test fun optionalContactsAndStudentIdRemainOptional() {
        val member = TeamMember(name = "Optional contacts example", studentId = "", email = " ", phone = " ")
        assertTrue(MemberValidation.errors(member).valid)
        repository.saveMember(member)
        val saved = repository.members().single { it.name == member.name }
        assertEquals("", saved.email)
        assertEquals("", saved.phone)
    }
}
