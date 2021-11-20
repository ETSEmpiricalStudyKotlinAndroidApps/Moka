package io.github.tonnyl.moka.data

import io.tonnyl.moka.common.data.Actor
import io.tonnyl.moka.graphql.fragment.Actor as RawActor

fun RawActor.toNonNullActor(): Actor {
    return Actor(avatarUrl, login, url)
}