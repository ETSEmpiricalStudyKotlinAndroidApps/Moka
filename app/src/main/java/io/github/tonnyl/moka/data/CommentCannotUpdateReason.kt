package io.github.tonnyl.moka.data

enum class CommentCannotUpdateReason {

    /**
     * You must be the author or have write access to this repository to update this comment.
     */
    INSUFFICIENT_ACCESS,

    /**
     * Unable to create comment because issue is locked.
     */
    LOCKED,

    /**
     * You must be logged in to update this comment.
     */
    LOGIN_REQUIRED,

    /**
     * Repository is under maintenance.
     */
    MAINTENANCE,

    /**
     * At least one email address must be verified to update this comment.
     */
    VERIFIED_EMAIL_REQUIRED,

    /**
     * You cannot update this comment
     */
    DENIED,

}