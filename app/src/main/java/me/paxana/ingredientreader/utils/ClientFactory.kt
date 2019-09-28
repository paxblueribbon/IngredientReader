package me.paxana.ingredientreader.utils

import android.content.Context
import com.amazonaws.mobile.config.AWSConfiguration
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.amazonaws.mobileconnectors.appsync.sigv4.BasicCognitoUserPoolsAuthProvider
import com.amazonaws.mobileconnectors.cognitoidentityprovider.CognitoUserPool

object ClientFactory {

    @Volatile private var client: AWSAppSyncClient? = null

    @Synchronized fun init(context: Context) {
        if (client == null)
        {
            val awsConfiguration = AWSConfiguration(context)
            client = AWSAppSyncClient.builder()
                .context(context)
                .awsConfiguration(awsConfiguration)
                .cognitoUserPoolsAuthProvider(BasicCognitoUserPoolsAuthProvider(CognitoUserPool(context, awsConfiguration))).build()
        }
    }
    @Synchronized fun appSyncClient():AWSAppSyncClient? {
        return client
    }
}