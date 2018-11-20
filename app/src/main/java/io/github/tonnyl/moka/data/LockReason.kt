package io.github.tonnyl.moka.data

/**
 * The possible reasons that an issue or pull request was locked.
 */
enum class LockReason {

    /**
     * The issue or pull request was locked because the conversation was off-topic.
     */
    OFF_TOPIC,

    /**
     * The issue or pull request was locked because the conversation was resolved.
     */
    RESOLVED,

    /**
     * The issue or pull request was locked because the conversation was spam.
     */
    SPAM,

    /**
     * The issue or pull request was locked because the conversation was too heated.
     */
    TOO_HEATED,

}