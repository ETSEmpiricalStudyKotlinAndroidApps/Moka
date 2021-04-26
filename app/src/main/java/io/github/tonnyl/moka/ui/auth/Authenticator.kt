package io.github.tonnyl.moka.ui.auth

import android.accounts.*
import android.content.Context
import android.content.Intent
import android.os.Bundle

class Authenticator(
    private val context: Context
) : AbstractAccountAuthenticator(context) {

    companion object {

        const val KEY_ACCOUNT_TYPE = "io.github.tonnyl.moka"
        const val KEY_AUTH_TOKEN = "KEY_AUTH_TOKEN"
        const val KEY_AUTH_USER_INFO = "KEY_AUTH_USER_INFO"

    }

    override fun editProperties(
        response: AccountAuthenticatorResponse,
        accountType: String
    ): Bundle? = null

    @Throws(NetworkErrorException::class)
    override fun addAccount(
        response: AccountAuthenticatorResponse,
        accountType: String,
        authTokenType: String?,
        requiredFeatures: Array<String>?,
        options: Bundle?
    ): Bundle? {
        val intent = Intent(context, AuthActivity::class.java)
        intent.putExtra(KEY_ACCOUNT_TYPE, accountType)
        intent.putExtra(KEY_AUTH_TOKEN, authTokenType)
        intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)

        val bundle = Bundle()
        bundle.putParcelable(AccountManager.KEY_INTENT, intent)

        return bundle
    }

    @Throws(NetworkErrorException::class)
    override fun confirmCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        options: Bundle
    ): Bundle? = null

    @Throws(NetworkErrorException::class)
    override fun getAuthToken(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ): Bundle {
        val am = AccountManager.get(context)
        val authToken = am.peekAuthToken(account, authTokenType)

        if (!authToken.isNullOrEmpty()) {
            return Bundle().apply {
                putString(AccountManager.KEY_ACCOUNT_NAME, account.name)
                putString(AccountManager.KEY_ACCOUNT_TYPE, account.type)
                putString(AccountManager.KEY_AUTHTOKEN, authToken)
            }
        }

        val intent = Intent(context, AuthActivity::class.java).apply {
            putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response)
            putExtra(KEY_ACCOUNT_TYPE, account.type)
            putExtra(KEY_AUTH_TOKEN, authTokenType)
        }

        return Bundle().apply {
            putParcelable(AccountManager.KEY_INTENT, intent)
        }
    }

    override fun getAuthTokenLabel(authTokenType: String): String? = null

    @Throws(NetworkErrorException::class)
    override fun updateCredentials(
        response: AccountAuthenticatorResponse,
        account: Account,
        authTokenType: String,
        options: Bundle
    ): Bundle? = null

    @Throws(NetworkErrorException::class)
    override fun hasFeatures(
        response: AccountAuthenticatorResponse,
        account: Account,
        features: Array<String>
    ): Bundle? = null

    @Throws(NetworkErrorException::class)
    override fun getAccountRemovalAllowed(
        response: AccountAuthenticatorResponse,
        account: Account
    ): Bundle {
        return super.getAccountRemovalAllowed(response, account)
    }

}
