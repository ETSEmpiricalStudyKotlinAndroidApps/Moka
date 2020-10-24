package io.github.tonnyl.moka.data

import android.net.Uri
import io.github.tonnyl.moka.fragment.CodeOfConduct as RawCodeOfConduct

/**
 * The Code of Conduct for a repository.
 */
data class CodeOfConduct(

    /**
     * The body of the CoC.
     */
    val body: String?,

    /**
     * The key for the CoC.
     */
    val key: String,

    /**
     * The formal name of the CoC.
     */
    val name: String,

    /**
     * The path to the CoC.
     */
    val url: Uri?

)

fun RawCodeOfConduct.toNonNullCodeOfConduct(): CodeOfConduct {
    return CodeOfConduct(body, key, name, url)
}