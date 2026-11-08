package com.comp3074.foodquest.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quests")
data class Quest(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val place: String,
    val address: String,
    val task: String,
    val tags: List<String>,
    val points: Int,
    val rating: Int = 0,
    val completed: Boolean = false,
    val difficulty: String = "Easy"
)

@Entity(tableName = "members")
data class TeamMember(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val studentId: String,
    val email: String = "",
    val phone: String = ""
)
