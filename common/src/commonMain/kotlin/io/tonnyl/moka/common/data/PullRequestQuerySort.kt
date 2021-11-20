package io.tonnyl.moka.common.data

enum class PullRequestQuerySort(val rawValue: String) {

    Created("created"),

    Updated("updated"),

    /**
     * comment count
     */
    Popularity("popularity"),

    /**
     * age, filtering by pulls updated in the last month.
     */
    LongRunning("long-running")

}