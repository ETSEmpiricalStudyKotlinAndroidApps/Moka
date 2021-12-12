package io.github.tonnyl.moka.parcelization

import android.os.Parcel
import kotlinx.datetime.Instant
import kotlinx.parcelize.Parceler

object InstantParceler : Parceler<Instant?> {

    override fun create(parcel: Parcel): Instant? {
        return parcel.readString()?.let {
            runCatching {
                Instant.parse(it)
            }.getOrNull()
        }
    }

    override fun Instant?.write(parcel: Parcel, flags: Int) {
        parcel.writeString(this?.toString())
    }

}