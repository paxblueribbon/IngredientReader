package me.paxana.ingredientreader.utils

import android.content.Context
import android.content.Intent
import me.paxana.ingredientreader.activities.ProfileActivity
import me.paxana.ingredientreader.activities.AuthenticationActivity
import me.paxana.ingredientreader.activities.ResultsActivity

fun Context.resultsActivityIntent(uri: String): Intent {
    return Intent(this, ResultsActivity::class.java).apply {
        putExtra("INTENT_URI_ID", uri)
    }
}

fun Context.profileActivityIntent(): Intent {
    return Intent(this, ProfileActivity::class.java)
}

fun Context.authenticationActivityIntent(): Intent {
    return Intent(this, AuthenticationActivity::class.java)
}

