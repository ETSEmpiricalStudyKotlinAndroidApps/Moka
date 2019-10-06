package io.github.tonnyl.moka.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * Information about a signature (GPG or S/MIME) on a Commit or Tag.
 */
@Parcelize
data class GitSignature(

    /**
     * Email used to sign this object.
     */
    val email: String,

    /**
     * True if the signature is valid and verified by GitHub.
     */
    val isValid: Boolean,

    /**
     * Payload for GPG signing object. Raw ODB object without the signature header.
     */
    val payload: String,

    /**
     * ASCII-armored signature header from object.
     */
    val signature: String,

    /**
     * GitHub user corresponding to the email signing this commit.
     */
    val signer: User?,

    /**
     * The state of this signature. VALID if signature is valid and verified by GitHub, otherwise represents reason why signature is considered invalid.
     */
    val state: GitSignatureState,

    /**
     * True if the signature was made with GitHub's signing key.
     */
    val wasSignedByGitHub: Boolean

) : Parcelable