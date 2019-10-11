package me.paxana.ingredientreader.activities

import android.os.Bundle
import android.widget.Toast
import com.amazonaws.mobile.client.AWSMobileClient
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool
import com.amazonaws.mobileconnectors.cognitoidentityprovider.handlers.GenericHandler
import com.orhanobut.logger.Logger
import kotlinx.android.synthetic.main.activity_profile.*
import java.lang.Exception
import android.content.Intent
import com.pixplicity.easyprefs.library.Prefs
import me.paxana.ingredientreader.BaseActivity
import me.paxana.ingredientreader.R
import me.paxana.ingredientreader.utils.instantiate


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
                            Toast.makeText(applicationContext, "Password Successfully Changed", Toast.LENGTH_SHORT).show()
                        }

                        override fun onFailure(exception: Exception?) {
                            //TODO: [add_profile_activity] HANDLE SPECIFIC EXCEPTIONS IN MORE USER FRIENDLY WAYS
                            Toast.makeText(applicationContext, "Error: ${exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                            Logger.e("Err: %s", exception?.localizedMessage)
                        }
                    })
                }
                else {
                    Toast.makeText(applicationContext, "New Password Must Be More Than 6 Characters", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(applicationContext, "New Password Fields Do Not Match", Toast.LENGTH_SHORT).show()
            }
        }

        logout.setOnClickListener {
            Logger.d("LOGOUT CLICKED")
            val broadcastIntent = Intent()
            broadcastIntent.action = "com.package.ACTION_LOGOUT"
            sendBroadcast(broadcastIntent)
        }

        //TODO: [add_profile_activity] MAKE INSTANTIATIONS WORK WITH STRING RESOURCES RATHER THAN STRING LITERALS

        debug_toggle.instantiate("DEBUG_KEY", false)
        sugar_toggle.instantiate("SUGAR_VEGAN_KEY", true)
        palm_toggle.instantiate("PALM_VEGAN_KEY", true)
    }
}
