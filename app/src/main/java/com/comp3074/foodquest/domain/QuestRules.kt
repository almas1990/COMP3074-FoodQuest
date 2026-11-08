package com.comp3074.foodquest.domain

import com.comp3074.foodquest.data.Quest

object QuestRules {
    fun filter(quests: List<Quest>, query: String, tag: String?): List<Quest> = quests.filter { quest ->
        (query.isBlank() || quest.name.contains(query.trim(), true) || quest.difficulty.contains(query.trim(), true) || quest.tags.any { it.contains(query.trim(), true) }) &&
            (tag == null || quest.tags.any { it.equals(tag, true) })
    }

    fun points(quests: List<Quest>): Int = quests.filter { it.completed }.sumOf { it.points }
    fun tags(input: String): List<String> = input.split(',').map(String::trim).filter(String::isNotEmpty).distinctBy { it.lowercase() }
}
