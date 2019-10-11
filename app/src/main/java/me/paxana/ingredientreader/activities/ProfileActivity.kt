package me.paxana.ingredientreader.activities

import android.os.Bundle
import android.widget.Toast
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler
import com.orhanobut.logger.Logger
import com.pixplicity.easyprefs.library.Prefs
import kotlinx.android.synthetic.main.activity_profile.*
import java.lang.Exception
import android.content.Intent
import me.paxana.ingredientreader.BaseActivity
import me.paxana.ingredientreader.R


class ProfileActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        val awsConfiguration = AWSConfiguration(this)
        val cognitoUserPool = CognitoUserPool(this, awsConfiguration)

        textView.text = getString(R.string.profile_greeting, AWSMobileClient.getInstance().username)

        submit_button.setOnClickListener {

            if (new_PW.editText!!.text.toString() == new_PW_confirm.editText!!.text.toString()) {
                if (new_PW.editText!!.text.length >= 6){
                    cognitoUserPool.currentUser.changePasswordInBackground(old_PW.editText!!.text.toString(), new_PW_confirm.editText!!.text.toString(), object : GenericHandler{
                        override fun onSuccess() {
                            Toast.makeText(applicationContext, "OMG U DID IT", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(exception: Exception?) {
                            //TODO HANDLE SPECIFIC EXCEPTIONS IN MORE USER FRIENDLY WAYS
                            Toast.makeText(applicationContext, "Error: ${exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                            Logger.e("Err: %s", exception?.localizedMessage)
                        }
                    })
                }
                else {
                    Toast.makeText(applicationContext, "New Password Must Be More Than 6 Characters", Toast.LENGTH_SHORT).show()
                }
            }
        }

        logout.setOnClickListener {
            Logger.d("LOGOUT CLICKED")
            val broadcastIntent = Intent()
            broadcastIntent.action = "com.package.ACTION_LOGOUT"
            sendBroadcast(broadcastIntent)
        }

        //TODO: INSTANTIATE TOGGLES WITH ONE LINE

        debug_toggle.isChecked = Prefs.getBoolean(getString(R.string.SP_debug_key), false)
        sugar_toggle.isChecked = Prefs.getBoolean(getString(R.string.SP_sugar_vegan_key), true)
        palm_toggle.isChecked = Prefs.getBoolean(getString(R.string.SP_palm_vegan_key), true)

        debug_toggle.setOnCheckedChangeListener { _, isChecked ->
            Prefs.putBoolean(getString(R.string.SP_debug_key), isChecked)
        }
        sugar_toggle.setOnCheckedChangeListener { _, isChecked ->
            Prefs.putBoolean(getString(R.string.SP_sugar_vegan_key), isChecked)
        }
        palm_toggle.setOnCheckedChangeListener { _, isChecked ->
            Prefs.putBoolean(getString(R.string.SP_palm_vegan_key), isChecked)
        }


    }
}
