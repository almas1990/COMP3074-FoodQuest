package com.comp3074.foodquest.ui

import android.os.Bundle
import android.view.View
import com.comp3074.foodquest.domain.QuestRules

class AchievementsFragment : ScreenFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        model.quests.observe(viewLifecycleOwner) { quests ->
            body.removeAllViews()
            heading(body, "Achievements")
            val points = QuestRules.points(quests)
            val completed = quests.count { it.completed }
            heading(body, "$points points")
            label(body, "$completed of ${quests.size} quests completed")
            label(body, if (points >= 50) "Taste Explorer - unlocked" else "Taste Explorer - 50 points")
            label(body, if (completed >= 3) "Food Quest Champion - unlocked" else "Food Quest Champion - complete 3 quests")
            quests.filter { it.completed }.forEach { label(body, "${it.name} - ${it.points} pts") }
        }
    }
}
