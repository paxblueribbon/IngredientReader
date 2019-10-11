package me.paxana.ingredientreader

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.amazonaws.mobile.client.AWSMobileClient
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_profile.*

class ProfileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        textView.text = "Hello ${AWSMobileClient.getInstance().username}!"

        debug_toggle.isChecked = Prefs.getBoolean("DEBUG_KEY", false)
        sugar_toggle.isChecked = Prefs.getBoolean("SUGAR_KEY", true)
        palm_toggle.isChecked = Prefs.getBoolean("PALM_KEY", true)

        debug_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            Prefs.putBoolean("DEBUG_KEY", isChecked)
        }
        sugar_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            Prefs.putBoolean("SUGAR_KEY", isChecked)
        }
        palm_toggle.setOnCheckedChangeListener { buttonView, isChecked ->
            Prefs.putBoolean("PALM_KEY", isChecked)
        }

    }
}
