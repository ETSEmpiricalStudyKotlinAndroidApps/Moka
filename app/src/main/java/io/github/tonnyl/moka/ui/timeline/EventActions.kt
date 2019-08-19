package io.github.tonnyl.moka.ui.timeline

import io.github.tonnyl.moka.data.Event
import io.github.tonnyl.moka.ui.profile.ProfileType

interface EventActions {

    fun openProfile(login: String, profileType: ProfileType)

    fun openEventDetails(event: Event)

}