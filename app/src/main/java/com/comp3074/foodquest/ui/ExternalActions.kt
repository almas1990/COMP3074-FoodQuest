package com.comp3074.foodquest.ui

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object ExternalActions {
    fun email(context: Context, recipient: String, subject: String, body: String) {
        // mailto is an opaque URI; a hierarchical URI builder can drop its recipient.
        val uri = Uri.parse("mailto:${Uri.encode(recipient, "@")}?subject=${Uri.encode(subject)}&body=${Uri.encode(body)}")
        launch(context, Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        })
    }

    fun sms(context: Context, phone: String) = launch(context, Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + Uri.encode(phone))))
    fun phone(context: Context, phone: String) = launch(context, Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + Uri.encode(phone))))
    fun directions(context: Context, address: String) = launch(context, Intent(Intent.ACTION_VIEW,
        Uri.parse("https://www.google.com/maps/dir/").buildUpon().appendQueryParameter("api", "1")
            .appendQueryParameter("destination", address).build()))

    fun launch(context: Context, intent: Intent) {
        try { context.startActivity(intent) }
        catch (_: ActivityNotFoundException) { Toast.makeText(context, "No application is installed for this action.", Toast.LENGTH_LONG).show() }
    }
}
