package com.comp3074.foodquest.ui

import android.os.Bundle
import android.view.View

class AboutFragment : ScreenFragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        heading(body, "About Food Quest")
        label(body, "Discover local food through challenges, ratings, and shared recommendations.")
        heading(body, "COMP3074 - Team P23")
        label(body, "Almas Sultana - 101564373\nArcadio Vega - 101569135\nJuela Topi - 101087887")
        label(body, "Instructor: Przemyslaw Pawluk")
    }
}
