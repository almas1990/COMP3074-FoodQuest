package com.comp3074.foodquest.ui

import com.comp3074.foodquest.R

import android.os.Bundle
import android.text.InputType
import android.view.View
import androidx.core.os.bundleOf
import com.comp3074.foodquest.data.Quest
import com.comp3074.foodquest.domain.QuestRules

class QuestFormFragment : ScreenFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val questId = arguments?.getLong("questId") ?: 0L
        val existing = model.quests.value.orEmpty().find { it.id == questId }
        back(body)
        heading(body, if (questId == 0L) "Add quest" else "Edit quest")
        if (questId != 0L && existing == null) {
            label(body, "Quest is no longer available.")
            return
        }
        val name = field(body, "Quest name", existing?.name.orEmpty(), R.id.quest_name)
        val place = field(body, "Food place", existing?.place.orEmpty(), R.id.quest_place)
        val address = field(body, "Street, city, province, postal code", existing?.address.orEmpty(), R.id.quest_address, InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_POSTAL_ADDRESS)
        val task = field(body, "Challenge", existing?.task.orEmpty(), R.id.quest_task, InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_MULTI_LINE)
        val tags = field(body, "Tags (comma separated)", existing?.tags?.joinToString(", ").orEmpty(), R.id.quest_tags)
        val points = field(body, "Points (1-10000)", existing?.points?.toString() ?: "50", R.id.quest_points, InputType.TYPE_CLASS_NUMBER)
        val difficulties = listOf("Easy", "Medium", "Hard")
        label(body, "Difficulty")
        val difficulty = android.widget.Spinner(requireContext()).apply {
            id = R.id.quest_difficulty
            adapter = android.widget.ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, difficulties)
            setSelection(difficulties.indexOf(existing?.difficulty ?: "Easy").coerceAtLeast(0))
        }
        body.addView(difficulty)
        val save = action(body, "Save quest") {
            var valid = true
            listOf(name, place, address, task).forEach {
                if (it.text.isBlank()) { it.error = "Required"; valid = false }
            }
            val value = points.text.toString().toIntOrNull()
            if (value == null || value !in 1..10000) { points.error = "Enter 1 to 10000"; valid = false }
            if (valid) {
                model.save(Quest(questId, name.text.toString(), place.text.toString(), address.text.toString(),
                    task.text.toString(), QuestRules.tags(tags.text.toString()), value!!,
                    existing?.rating ?: 0, existing?.completed ?: false, difficulty.selectedItem.toString())) {
                    if (isAdded) parentFragmentManager.popBackStack()
                }
            }
        }
        model.busy.observe(viewLifecycleOwner) { save.isEnabled = !it }
    }

    companion object {
        fun newInstance(id: Long) = QuestFormFragment().apply { arguments = bundleOf("questId" to id) }
    }
}
