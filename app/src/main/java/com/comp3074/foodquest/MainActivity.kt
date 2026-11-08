package com.comp3074.foodquest

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.activity.viewModels
import com.comp3074.foodquest.ui.*

class MainActivity : FragmentActivity() {
    private val model: QuestViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val nav = findViewById<LinearLayout>(R.id.navigation)
        listOf(
            R.string.quests to { QuestListFragment() },
            R.string.points to { AchievementsFragment() },
            R.string.team to { TeamFragment() },
            R.string.about to { AboutFragment() }
        ).forEach { (label, screen) ->
            nav.addView(Button(this).apply {
                setText(label)
                setOnClickListener { openRoot(screen()) }
            }, LinearLayout.LayoutParams(0, -2, 1f))
        }
        if (savedInstanceState == null) openRoot(SplashFragment())
        model.error.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                model.clearError()
            }
        }
    }

    fun open(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.screen_container, fragment)
            .addToBackStack(null).commit()
    }

    fun openRoot(fragment: Fragment) {
        supportFragmentManager.popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction().replace(R.id.screen_container, fragment).commit()
    }
}

