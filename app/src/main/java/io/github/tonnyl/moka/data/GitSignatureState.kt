package io.github.tonnyl.moka.data

/**
 * The state of a Git signature.
 */
enum class GitSignatureState {

    /**
     * The signing certificate or its chain could not be verified.
     */
    BAD_CERT,

    /**
     * Invalid email used for signing.
     */
    BAD_EMAIL,

    /**
     * Signing key expired.
     */
    EXPIRED_KEY,

    /**
     * Internal error - the GPG verification service misbehaved.
     */
    GPGVERIFY_ERROR,

    /**
     * Internal error - the GPG verification service is unavailable at the moment
     */
    GPGVERIFY_UNAVAILABLE,

    /**
     * Invalid signature.
     */
    INVALID,

    /**
     * Malformed signature.
     */
    MALFORMED_SIG,

    /**
     * The usage flags for the key that signed this don't allow signing.
     */
    NOT_SIGNING_KEY,

    /**
     * Email used for signing not known to GitHub.
     */
    NO_USER,

    /**
     * Valid siganture, though certificate revocation check failed.
     */
    OCSP_ERROR,

    /**
     * Valid signature, pending certificate revocation checking.
     */
    OCSP_PENDING,

    /**
     * One or more certificates in chain has been revoked.
     */
    OCSP_REVOKED,

    /**
     * Key used for signing not known to GitHub.
     */
    UNKNOWN_KEY,

    /**
     * Unknown signature type.
     */
    UNKNOWN_SIG_TYPE,

    /**
     * Unsigned.
     */
    UNSIGNED,

    /**
     * Email used for signing unverified on GitHub.
     */
    UNVERIFIED_EMAIL,

    /**
     * Valid signature and verified by GitHub.
     */
    VALID,

}