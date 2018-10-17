package io.github.tonnyl.moka.data

enum class RepositoryPermission {

    /**
     * Can read, clone, push, and add collaborators.
     */
    ADMIN,

    /**
     * Can read and clone.
     */
    READ,

    /**
     * Can read, clone and push.
     */
    WRITE

}