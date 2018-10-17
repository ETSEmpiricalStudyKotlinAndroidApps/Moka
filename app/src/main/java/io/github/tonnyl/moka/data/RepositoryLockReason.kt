package io.github.tonnyl.moka.data

/**
 * The possible reasons a given repository could be in a locked state.
 */
enum class RepositoryLockReason {

    /**
     * The repository is locked due to a billing related reason.
     */
    BILLING,

    /**
     * The repository is locked due to a migration.
     */
    MIGRATING,

    /**
     * The repository is locked due to a move.
     */
    MOVING,

    /**
     * The repository is locked due to a rename.
     */
    RENAME

}