package com.comp3074.foodquest.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import com.comp3074.foodquest.R

class SplashFragment : ScreenFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        body.addView(ImageView(requireContext()).apply {
            setImageResource(R.drawable.ic_food_quest)
            contentDescription = "Food Quest logo"
        }, android.widget.LinearLayout.LayoutParams(-1, (120 * resources.displayMetrics.density).toInt()))
        heading(body, "Food Quest")
        label(body, "Explore a place. Complete a challenge. Earn your next badge.")
        action(body, "Explore quests") { host.openRoot(QuestListFragment()) }
    }
}
