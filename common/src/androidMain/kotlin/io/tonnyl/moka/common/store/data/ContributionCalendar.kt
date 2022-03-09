package io.tonnyl.moka.common.store.data

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable
import kotlinx.serialization.protobuf.ProtoNumber

@Serializable
data class ContributionCalendar(

    @ProtoNumber(1)
    val colors: List<String> = emptyList(),

    @ProtoNumber(2)
    val isHalloween: Boolean = false,

    @ProtoNumber(3)
    val months: List<ContributionCalendarMonth> = emptyList(),

    @ProtoNumber(4)
    val totalContributions: Int = 0,

    @ProtoNumber(5)
    val weeks: List<ContributionCalendarWeek> = emptyList()

)

@Serializable
data class ContributionCalendarMonth(

    @ProtoNumber(1)
    val firstDay: LocalDate = LocalDate.parse("2020-12-01"),

    @ProtoNumber(2)
    val name: String = "",

    @ProtoNumber(3)
    val totalWeeks: Int = 0,

    @ProtoNumber(4)
    val year: Int = 0

)

@Serializable
data class ContributionCalendarDay(

    @ProtoNumber(1)
    val color: String = "",

    @ProtoNumber(2)
    val contributionCount: Int = 0,

    @ProtoNumber(3)
    val contributionLevel: String = "",

    @ProtoNumber(4)
    val date: LocalDate = LocalDate.parse("2020-12-01"),

    @ProtoNumber(5)
    val weekday: Int = 0

)

@Serializable
data class ContributionCalendarWeek(

    @ProtoNumber(1)
    val contributionDays: List<ContributionCalendarDay> = emptyList(),

    @ProtoNumber(2)
    val firstDay: LocalDate = LocalDate.parse("2020-12-01")

)