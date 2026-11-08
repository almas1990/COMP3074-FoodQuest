package com.comp3074.foodquest.data

class QuestRepository(private val database: FoodQuestDatabase) {
    private val dao = database.questDao()

    fun quests(): List<Quest> = dao.quests()

    fun save(quest: Quest): Long {
        require(quest.name.isNotBlank() && quest.place.isNotBlank() && quest.address.isNotBlank() && quest.task.isNotBlank())
        require(quest.points in 1..10000 && quest.rating in 0..5)
        require(quest.difficulty in listOf("Easy", "Medium", "Hard"))
        val clean = quest.copy(name = quest.name.trim(), place = quest.place.trim(),
            address = quest.address.trim(), task = quest.task.trim())
        return if (quest.id == 0L) dao.insert(clean) else {
            check(dao.update(clean) == 1)
            quest.id
        }
    }

    fun delete(id: Long) = dao.delete(id)
    fun complete(id: Long) = dao.complete(id)
    fun rate(id: Long, rating: Int) {
        require(rating in 1..5)
        dao.rate(id, rating)
    }

    fun members(): List<TeamMember> = dao.members()

    fun saveMember(member: TeamMember) {
        require(member.name.isNotBlank())
        val clean = member.copy(name = member.name.trim(), studentId = member.studentId.trim(),
            email = member.email.trim(), phone = member.phone.trim())
        if (clean.id == 0L) dao.insertMember(clean)
        else check(dao.updateMember(clean) == 1)
    }

    fun close() = database.close()
}
