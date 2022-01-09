package io.github.tonnyl.moka.ui.auth

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class AuthParameter(

    val code: String,

    val state: String

) : Parcelable