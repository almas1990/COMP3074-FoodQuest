package com.comp3074.foodquest

import android.Manifest
import android.graphics.Bitmap
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.platform.app.InstrumentationRegistry
import com.comp3074.foodquest.data.FoodQuestDatabase
import com.comp3074.foodquest.data.Quest
import com.comp3074.foodquest.data.QuestRepository
import com.comp3074.foodquest.ui.MapFragment
import com.comp3074.foodquest.ui.QuestViewModel
import com.google.android.gms.maps.SupportMapFragment
import org.junit.Assert.assertTrue
import org.junit.Assume.assumeTrue
import org.junit.Test
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

class MapWorkflowTest {
    @Test fun addressMarkerAndFullScreenMapRender() {
        assumeTrue("A locally configured Maps key is required", BuildConfig.MAPS_CONFIGURED)
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val repository = QuestRepository(FoodQuestDatabase.open(context))
        val questId = repository.save(Quest(name = "Map verification", place = "Campus food challenge",
            address = "200 King Street East, Toronto, ON", task = "Find a lunch option near campus",
            tags = listOf("Lunch"), points = 50))
        val embeddedLoaded = CountDownLatch(1)
        val fullscreenLoaded = CountDownLatch(1)
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        try {
            scenario.onActivity { activity ->
                activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
                    object : FragmentManager.FragmentLifecycleCallbacks() {
                        override fun onFragmentViewCreated(manager: FragmentManager, fragment: Fragment,
                            view: View, savedInstanceState: Bundle?) {
                            if (fragment !is SupportMapFragment) return
                            val fullscreen = fragment.tag == "fullmap"
                            var captured = false
                            fragment.getMapAsync { map ->
                                map.setOnMapLoadedCallback {
                                    val target = map.cameraPosition.target
                                    // The initial SDK camera is not the geocoded quest destination.
                                    if (!captured && target.latitude in 43.5..43.9 && target.longitude in -79.8..-79.0) {
                                        captured = true
                                        map.snapshot { bitmap ->
                                            if (bitmap != null && hasMapDetail(bitmap)) {
                                                val name = if (fullscreen) "map-fullscreen.png" else "map-embedded.png"
                                                File(context.getExternalFilesDir(null), name).outputStream().use {
                                                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
                                                }
                                                if (fullscreen) fullscreenLoaded.countDown() else embeddedLoaded.countDown()
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }, true)
                val model = ViewModelProvider(activity)[QuestViewModel::class.java]
                var opened = false
                model.quests.observe(activity) { quests ->
                    if (!opened && quests.any { it.id == questId }) {
                        opened = true
                        activity.openRoot(MapFragment.newInstance(questId))
                    }
                }
            }
            assertTrue("Embedded map must load tiles at the quest address", embeddedLoaded.await(45, TimeUnit.SECONDS))
            onView(withText("Full-screen map")).perform(scrollTo(), click())
            assertTrue("Full-screen map must load tiles at the quest address", fullscreenLoaded.await(45, TimeUnit.SECONDS))
            onView(withText("Close map")).perform(click())
            val automation = InstrumentationRegistry.getInstrumentation().uiAutomation
            automation.grantRuntimePermission(context.packageName, Manifest.permission.ACCESS_COARSE_LOCATION)
            automation.grantRuntimePermission(context.packageName, Manifest.permission.ACCESS_FINE_LOCATION)
            onView(withText("Show my location")).perform(scrollTo(), click())
            val locationEnabled = CountDownLatch(1)
            scenario.onActivity { activity ->
                val screen = activity.supportFragmentManager.findFragmentById(R.id.screen_container) as MapFragment
                val sdk = screen.childFragmentManager.findFragmentByTag("map") as SupportMapFragment
                sdk.getMapAsync { map -> if (map.isMyLocationEnabled) locationEnabled.countDown() }
            }
            assertTrue("Granted location permission must enable the map location layer", locationEnabled.await(10, TimeUnit.SECONDS))
        } finally {
            scenario.close()
            repository.delete(questId)
            repository.close()
        }
    }

    private fun hasMapDetail(bitmap: Bitmap): Boolean {
        val colors = mutableSetOf<Int>()
        for (y in 0 until bitmap.height step 16) {
            for (x in 0 until bitmap.width step 16) colors.add(bitmap.getPixel(x, y))
        }
        return colors.size > 30
    }
}
