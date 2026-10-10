package com.comp3074.foodquest

import android.app.Activity
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class MainActivity : Activity() {
    private enum class Screen {
        Splash, QuestList, QuestDetail, AddQuest, Map, Achievements, About
    }

    private data class FoodQuest(
        val id: Int,
        val name: String,
        val place: String,
        val address: String,
        val task: String,
        val tags: List<String>,
        val difficulty: String,
        val points: Int,
        val rating: Int,
        val completed: Boolean = false
    )

    private val green = Color.rgb(31, 122, 77)
    private val ink = Color.rgb(23, 35, 29)
    private val card = Color.WHITE
    private val paleGreen = Color.rgb(229, 243, 235)

    private lateinit var root: LinearLayout
    private var screen = Screen.Splash
    private var selectedQuestId = 1
    private var searchQuery = ""
    private var selectedTag = "All"
    private var nextQuestId = 4

    private val quests = mutableListOf(
        FoodQuest(
            id = 1,
            name = "Taco Trail",
            place = "Any local taco spot",
            address = "Near campus or downtown",
            task = "Find a taco place you have not tried before, rate it, and save the best tag.",
            tags = listOf("Budget", "Dinner", "New Cuisine"),
            difficulty = "Easy",
            points = 50,
            rating = 4
        ),
        FoodQuest(
            id = 2,
            name = "Dessert Discovery",
            place = "Dessert cafe or bakery",
            address = "Student-friendly food area",
            task = "Visit a dessert spot, record the address, and share the recommendation with a friend.",
            tags = listOf("Dessert", "Share", "Cafe"),
            difficulty = "Easy",
            points = 40,
            rating = 5
        ),
        FoodQuest(
            id = 3,
            name = "Green Plate Challenge",
            place = "Restaurant with vegetarian option",
            address = "Any nearby food court or restaurant",
            task = "Find a vegetarian or vegan meal option and rate how easy it was to find.",
            tags = listOf("Vegan", "Healthy", "Lunch"),
            difficulty = "Medium",
            points = 60,
            rating = 3
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = green

        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(Color.rgb(250, 250, 247))
        }
        setContentView(root)
        render()
    }

    private fun render() {
        root.removeAllViews()
        root.addView(topBar())

        val scroll = ScrollView(this)
        val body = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(18), dp(16), dp(18), dp(22))
        }
        scroll.addView(body)
        root.addView(scroll, LinearLayout.LayoutParams(-1, 0, 1f))

        when (screen) {
            Screen.Splash -> renderSplash(body)
            Screen.QuestList -> renderQuestList(body)
            Screen.QuestDetail -> renderQuestDetail(body)
            Screen.AddQuest -> renderAddQuest(body)
            Screen.Map -> renderMap(body)
            Screen.Achievements -> renderAchievements(body)
            Screen.About -> renderAbout(body)
        }

        root.addView(bottomNav())
    }

    private fun topBar(): View {
        val bar = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setBackgroundColor(green)
            setPadding(dp(18), dp(14), dp(18), dp(12))
        }
        bar.addView(text("Food Quest", 24, Color.WHITE, Typeface.BOLD))
        bar.addView(text("COMP3074 UI Prototype - P23", 13, Color.rgb(231, 247, 238), Typeface.NORMAL))
        return bar
    }

    private fun bottomNav(): View {
        val nav = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(ink)
            setPadding(dp(6), dp(7), dp(6), dp(7))
        }
        navButton(nav, "Quests", Screen.QuestList)
        navButton(nav, "Add", Screen.AddQuest)
        navButton(nav, "Map", Screen.Map)
        navButton(nav, "Points", Screen.Achievements)
        navButton(nav, "About", Screen.About)
        return nav
    }

    private fun navButton(parent: LinearLayout, label: String, target: Screen) {
        val button = Button(this).apply {
            text = label
            textSize = 12f
            setTextColor(Color.WHITE)
            setBackgroundColor(if (screen == target) green else ink)
            setOnClickListener {
                screen = target
                render()
            }
        }
        parent.addView(button, LinearLayout.LayoutParams(0, dp(44), 1f))
    }

    private fun renderSplash(body: LinearLayout) {
        body.gravity = Gravity.CENTER_HORIZONTAL
        body.addView(spacer(44))
        body.addView(text("Food Quest", 34, green, Typeface.BOLD).center())
        body.addView(text("Discover food places through real-world quests.", 17, ink, Typeface.NORMAL).center())
        body.addView(spacer(24))
        body.addView(cardView {
            addView(text("Prototype goal", 20, ink, Typeface.BOLD))
            addView(text("Players browse food challenges, follow directions, complete tasks, earn points, and share completed quests.", 16, ink, Typeface.NORMAL))
            addView(spacer(12))
            addView(primaryButton("Start exploring quests") {
                screen = Screen.QuestList
                render()
            })
        })
    }

    private fun renderQuestList(body: LinearLayout) {
        body.addView(sectionTitle("Quest List"))
        body.addView(text("Search and filter food quests by name, cuisine, difficulty, or tag.", 15, ink, Typeface.NORMAL))
        body.addView(spacer(12))

        val search = EditText(this).apply {
            hint = "Search quests"
            setSingleLine(true)
            setText(searchQuery)
            textSize = 16f
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    searchQuery = s?.toString().orEmpty()
                    render()
                }
                override fun afterTextChanged(s: Editable?) = Unit
            })
        }
        body.addView(search, LinearLayout.LayoutParams(-1, dp(54)))
        body.addView(tagFilters())
        body.addView(spacer(8))

        filteredQuests().forEach { quest ->
            body.addView(questCard(quest))
            body.addView(spacer(10))
        }

        if (filteredQuests().isEmpty()) {
            body.addView(cardView {
                addView(text("No quests found", 18, ink, Typeface.BOLD))
                addView(text("Try another search term or use the All filter.", 15, ink, Typeface.NORMAL))
            })
        }
    }

    private fun tagFilters(): View {
        val row = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        listOf("All", "Budget", "Dessert", "Vegan", "Lunch").forEach { tag ->
            val button = Button(this).apply {
                text = tag
                textSize = 12f
                setTextColor(if (selectedTag == tag) Color.WHITE else ink)
                setBackgroundColor(if (selectedTag == tag) green else paleGreen)
                setOnClickListener {
                    selectedTag = tag
                    render()
                }
            }
            row.addView(button, LinearLayout.LayoutParams(0, dp(44), 1f))
        }
        return row
    }

    private fun questCard(quest: FoodQuest): View {
        return cardView {
            addView(text("${quest.name} - ${quest.points} pts", 20, ink, Typeface.BOLD))
            addView(text(quest.place, 15, green, Typeface.BOLD))
            addView(text(quest.task, 15, ink, Typeface.NORMAL))
            addView(text("Tags: ${quest.tags.joinToString(", ")}", 14, Color.DKGRAY, Typeface.NORMAL))
            addView(text("Difficulty: ${quest.difficulty} | Rating: ${quest.rating}/5 | ${if (quest.completed) "Completed" else "Available"}", 14, Color.DKGRAY, Typeface.NORMAL))
            addView(spacer(8))
            addView(primaryButton("Open quest") {
                selectedQuestId = quest.id
                screen = Screen.QuestDetail
                render()
            })
        }
    }

    private fun renderQuestDetail(body: LinearLayout) {
        val quest = selectedQuest()
        body.addView(sectionTitle("Quest Detail"))
        body.addView(cardView {
            addView(text(quest.name, 24, ink, Typeface.BOLD))
            addView(text("${quest.points} points | Difficulty: ${quest.difficulty}", 15, green, Typeface.BOLD))
            addView(spacer(10))
            addView(text("Task", 17, ink, Typeface.BOLD))
            addView(text(quest.task, 16, ink, Typeface.NORMAL))
            addView(spacer(10))
            addView(text("Food place", 17, ink, Typeface.BOLD))
            addView(text("${quest.place}\n${quest.address}", 16, ink, Typeface.NORMAL))
            addView(spacer(10))
            addView(text("Tags: ${quest.tags.joinToString(", ")}", 15, Color.DKGRAY, Typeface.NORMAL))
            addView(text("Rating: ${quest.rating}/5", 15, Color.DKGRAY, Typeface.NORMAL))
            addView(spacer(14))
            addView(primaryButton(if (quest.completed) "Quest completed" else "Mark complete and earn points") {
                if (!quest.completed) {
                    val index = quests.indexOfFirst { it.id == quest.id }
                    quests[index] = quest.copy(completed = true)
                    Toast.makeText(this@MainActivity, "+${quest.points} points earned", Toast.LENGTH_SHORT).show()
                }
                render()
            })
            addView(secondaryButton("View map and directions") {
                screen = Screen.Map
                render()
            })
            addView(secondaryButton("Share by email") {
                Toast.makeText(this@MainActivity, "Prototype: opens email composer in final app", Toast.LENGTH_LONG).show()
            })
        })
    }

    private fun renderAddQuest(body: LinearLayout) {
        body.addView(sectionTitle("Add / Edit Quest"))
        body.addView(text("Prototype form for creating a new food quest.", 15, ink, Typeface.NORMAL))
        body.addView(spacer(12))

        val name = input("Quest name", "Campus Pizza Hunt")
        val place = input("Food place", "Any pizza place near campus")
        val address = input("Address", "Near campus")
        val task = input("Task", "Find a pizza slice under a student budget and rate it.")
        val tags = input("Tags", "Budget, Lunch")
        val points = input("Points", "30")

        listOf(name, place, address, task, tags, points).forEach { body.addView(it) }
        body.addView(primaryButton("Save quest") {
            val savedQuest = FoodQuest(
                id = nextQuestId++,
                name = name.text.toString().ifBlank { "Untitled Quest" },
                place = place.text.toString().ifBlank { "Food location" },
                address = address.text.toString().ifBlank { "Address to be confirmed" },
                task = task.text.toString().ifBlank { "Complete the food challenge." },
                tags = tags.text.toString().split(",").map { it.trim() }.filter { it.isNotBlank() },
                difficulty = "Easy",
                points = points.text.toString().toIntOrNull() ?: 25,
                rating = 0
            )
            quests.add(savedQuest)
            selectedQuestId = savedQuest.id
            screen = Screen.QuestDetail
            render()
        })
    }

    private fun renderMap(body: LinearLayout) {
        val quest = selectedQuest()
        body.addView(sectionTitle("Map / Directions"))
        body.addView(cardView {
            addView(text("Selected quest", 16, ink, Typeface.BOLD))
            addView(text("${quest.name}\n${quest.address}", 17, ink, Typeface.NORMAL))
            addView(spacer(12))
            addView(mapPlaceholder())
            addView(spacer(12))
            addView(primaryButton("Get directions") {
                Toast.makeText(this@MainActivity, "Prototype: route opens with current location in final app", Toast.LENGTH_LONG).show()
            })
        })
    }

    private fun mapPlaceholder(): View {
        val box = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(16), dp(22), dp(16), dp(22))
            setBackgroundColor(Color.rgb(226, 237, 232))
        }
        box.addView(text("Map preview", 22, green, Typeface.BOLD).center())
        box.addView(text("Current location -> Quest pin\nGoogle Maps integration planned for functional prototype", 15, ink, Typeface.NORMAL).center())
        return box
    }

    private fun renderAchievements(body: LinearLayout) {
        val completed = quests.filter { it.completed }
        val points = completed.sumOf { it.points }
        body.addView(sectionTitle("Achievements"))
        body.addView(cardView {
            addView(text("$points points earned", 28, green, Typeface.BOLD))
            addView(text("${completed.size} of ${quests.size} quests completed", 17, ink, Typeface.NORMAL))
        })
        body.addView(spacer(12))
        body.addView(cardView {
            addView(text("Badges", 20, ink, Typeface.BOLD))
            addView(text(if (points >= 50) "Taste Explorer unlocked" else "Complete a quest to unlock Taste Explorer", 16, ink, Typeface.NORMAL))
            addView(text(if (completed.size >= 3) "Food Quest Champion unlocked" else "Complete 3 quests to unlock Food Quest Champion", 16, ink, Typeface.NORMAL))
        })
    }

    private fun renderAbout(body: LinearLayout) {
        body.addView(sectionTitle("About Food Quest"))
        body.addView(cardView {
            addView(text("Purpose", 20, ink, Typeface.BOLD))
            addView(text("Food Quest helps students discover food places through location-based challenges, points, ratings, and sharing.", 16, ink, Typeface.NORMAL))
            addView(spacer(14))
            addView(text("Team P23", 20, ink, Typeface.BOLD))
            addView(text("Almas Sultana - 101564373\nArcadio Vega - 101569135\nJuela Topi - 101087887", 16, ink, Typeface.NORMAL))
            addView(spacer(14))
            addView(text("Prototype status", 20, ink, Typeface.BOLD))
            addView(text("This UI prototype demonstrates all planned screens and working interactions for search/filter, quest completion, points, and add quest.", 16, ink, Typeface.NORMAL))
        })
    }

    private fun filteredQuests(): List<FoodQuest> {
        return quests.filter { quest ->
            val matchesSearch = searchQuery.isBlank() ||
                quest.name.contains(searchQuery, ignoreCase = true) ||
                quest.place.contains(searchQuery, ignoreCase = true) ||
                quest.tags.any { it.contains(searchQuery, ignoreCase = true) }
            val matchesTag = selectedTag == "All" || quest.tags.any { it.equals(selectedTag, ignoreCase = true) }
            matchesSearch && matchesTag
        }
    }

    private fun selectedQuest(): FoodQuest = quests.firstOrNull { it.id == selectedQuestId } ?: quests.first()

    private fun input(hintText: String, defaultText: String): EditText {
        return EditText(this).apply {
            hint = hintText
            setText(defaultText)
            textSize = 16f
            setSingleLine(false)
            setPadding(dp(10), dp(8), dp(10), dp(8))
        }
    }

    private fun cardView(content: LinearLayout.() -> Unit): LinearLayout {
        return LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(16), dp(14), dp(16), dp(14))
            setBackgroundColor(card)
            content()
        }.also {
            it.layoutParams = LinearLayout.LayoutParams(-1, -2).apply {
                setMargins(0, dp(6), 0, dp(8))
            }
        }
    }

    private fun sectionTitle(value: String): TextView = text(value, 26, green, Typeface.BOLD)

    private fun text(value: String, sp: Int, color: Int, style: Int): TextView {
        return TextView(this).apply {
            text = value
            textSize = sp.toFloat()
            setTextColor(color)
            typeface = Typeface.create(Typeface.DEFAULT, style)
            setLineSpacing(0f, 1.08f)
            setPadding(0, dp(3), 0, dp(5))
        }
    }

    private fun TextView.center(): TextView {
        gravity = Gravity.CENTER
        return this
    }

    private fun primaryButton(label: String, action: () -> Unit): Button {
        return Button(this).apply {
            text = label
            textSize = 15f
            setTextColor(Color.WHITE)
            setBackgroundColor(green)
            setOnClickListener { action() }
        }
    }

    private fun secondaryButton(label: String, action: () -> Unit): Button {
        return Button(this).apply {
            text = label
            textSize = 15f
            setTextColor(ink)
            setBackgroundColor(paleGreen)
            setOnClickListener { action() }
        }
    }

    private fun spacer(height: Int): View = View(this).apply {
        layoutParams = ViewGroup.LayoutParams(1, dp(height))
    }

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()
}

