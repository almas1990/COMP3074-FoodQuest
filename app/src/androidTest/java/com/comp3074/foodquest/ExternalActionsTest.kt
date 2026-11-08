package com.comp3074.foodquest

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.test.platform.app.InstrumentationRegistry
import com.comp3074.foodquest.ui.ExternalActions
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExternalActionsTest {
    private class CapturingContext(context: Context) : ContextWrapper(context) {
        lateinit var intent: Intent
        override fun startActivity(intent: Intent) {
            this.intent = intent
        }
    }

    @Test fun emailKeepsRecipientSubjectAndMultilineBody() {
        val context = CapturingContext(InstrumentationRegistry.getInstrumentation().targetContext)
        ExternalActions.email(context, "student@example.com", "Lunch & dessert", "Quest completed\n50 points")

        assertEquals(Intent.ACTION_SENDTO, context.intent.action)
        assertEquals("mailto:student@example.com?subject=Lunch%20%26%20dessert&body=Quest%20completed%0A50%20points",
            context.intent.data.toString())
        assertEquals("student@example.com", context.intent.getStringArrayExtra(Intent.EXTRA_EMAIL)?.single())
        assertEquals("Lunch & dessert", context.intent.getStringExtra(Intent.EXTRA_SUBJECT))
        assertEquals("Quest completed\n50 points", context.intent.getStringExtra(Intent.EXTRA_TEXT))
    }

    @Test fun contactAndDirectionsActionsKeepTheirDestinations() {
        val context = CapturingContext(InstrumentationRegistry.getInstrumentation().targetContext)
        ExternalActions.sms(context, "+1 416 555 0100")
        assertEquals(Intent.ACTION_SENDTO, context.intent.action)
        assertEquals("smsto", context.intent.data?.scheme)
        assertEquals("+1 416 555 0100", context.intent.data?.schemeSpecificPart)

        ExternalActions.phone(context, "+1 416 555 0100")
        assertEquals(Intent.ACTION_DIAL, context.intent.action)
        assertEquals("tel", context.intent.data?.scheme)
        assertEquals("+1 416 555 0100", context.intent.data?.schemeSpecificPart)

        ExternalActions.directions(context, "200 King Street East, Toronto, ON")
        assertEquals(Intent.ACTION_VIEW, context.intent.action)
        assertEquals("www.google.com", context.intent.data?.host)
        assertEquals("1", context.intent.data?.getQueryParameter("api"))
        assertEquals("200 King Street East, Toronto, ON", context.intent.data?.getQueryParameter("destination"))
        assertTrue(context.intent.data.toString().startsWith("https://"))
    }
}
