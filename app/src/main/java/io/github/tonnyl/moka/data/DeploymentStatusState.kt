package io.github.tonnyl.moka.data

/**
 * The possible states for a deployment status.
 */
enum class DeploymentStatusState {

    /**
     * The deployment experienced an error.
     */
    ERROR,

    /**
     * The deployment has failed.
     */
    FAILURE,

    /**
     * The deployment is inactive.
     */
    INACTIVE,

    /**
     * The deployment is pending.
     */
    PENDING,

    /**
     * The deployment was successful.
     */
    SUCCESS

}