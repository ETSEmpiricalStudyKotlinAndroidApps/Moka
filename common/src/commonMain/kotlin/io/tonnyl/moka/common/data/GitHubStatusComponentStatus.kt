package io.tonnyl.moka.common.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
enum class GitHubStatusComponentStatus {

    @SerialName("operational")
    Operational,

    @SerialName("degraded_performance")
    DegradedPerformance,

    @SerialName("partial_outage")
    PartialOutage,

    @SerialName("major_outage")
    MajorOutage,

    @SerialName("under_maintenance")
    UnderMaintenance

}