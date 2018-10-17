package io.github.tonnyl.moka.data

enum class SubscriptionState {

    /**
     * The User is never notified.
     */
    IGNORED,

    /**
     * The User is notified of all conversations.
     */
    SUBSCRIBED,

    /**
     * The User is only notified when particpating or @mentioned.
     */
    UNSUBSCRIBED

}