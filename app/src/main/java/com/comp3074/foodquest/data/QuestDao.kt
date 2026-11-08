package com.comp3074.foodquest.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface QuestDao {
    @Query("SELECT * FROM quests ORDER BY id DESC")
    fun quests(): List<Quest>

    @Insert
    fun insert(quest: Quest): Long

    @Update
    fun update(quest: Quest): Int

    @Query("DELETE FROM quests WHERE id = :id")
    fun delete(id: Long)

    @Query("UPDATE quests SET completed = 1 WHERE id = :id AND completed = 0")
    fun complete(id: Long)

    @Query("UPDATE quests SET rating = :rating WHERE id = :id")
    fun rate(id: Long, rating: Int)

    @Query("SELECT * FROM members ORDER BY id ASC")
    fun members(): List<TeamMember>

    @Insert
    fun insertMember(member: TeamMember): Long

    @Update
    fun updateMember(member: TeamMember): Int
}
