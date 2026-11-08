package com.comp3074.foodquest.ui

import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.comp3074.foodquest.MainActivity
import com.comp3074.foodquest.R

abstract class ScreenFragment(layout: Int = R.layout.fragment_screen) : Fragment(layout) {
    protected val model: QuestViewModel by activityViewModels()
    protected val host get() = requireActivity() as MainActivity
    protected val body get() = requireView().findViewById<LinearLayout>(R.id.screen_body)

    protected fun heading(parent: LinearLayout, title: String) = label(parent, title, R.style.ScreenHeading)
    protected fun label(parent: LinearLayout, value: String, style: Int = R.style.BodyText): TextView =
        TextView(requireContext(), null, 0, style).apply { text = value }.also { parent.addView(it) }

    protected fun action(parent: LinearLayout, title: String, onClick: () -> Unit): Button =
        Button(requireContext(), null, 0, R.style.ActionButton).apply {
            text = title
            setOnClickListener { onClick() }
        }.also { parent.addView(it, LinearLayout.LayoutParams(-1, -2)) }

    protected fun back(parent: LinearLayout) {
        action(parent, getString(R.string.back)) { parentFragmentManager.popBackStack() }
    }

    protected fun field(parent: LinearLayout, title: String, initial: String, key: Int,
                        type: Int = android.text.InputType.TYPE_CLASS_TEXT): EditText {
        label(parent, title)
        return EditText(requireContext()).apply {
            id = key
            hint = title
            setText(initial)
            inputType = type
            minHeight = (48 * resources.displayMetrics.density).toInt()
        }.also { parent.addView(it, LinearLayout.LayoutParams(-1, -2)) }
    }

    protected fun section(parent: LinearLayout): LinearLayout = LinearLayout(requireContext()).apply {
        orientation = LinearLayout.VERTICAL
        val padding = (12 * resources.displayMetrics.density).toInt()
        setPadding(padding, padding, padding, padding)
        setBackgroundColor(android.graphics.Color.WHITE)
    }.also { parent.addView(it, LinearLayout.LayoutParams(-1, -2).apply { bottomMargin = 16 }) }
}
