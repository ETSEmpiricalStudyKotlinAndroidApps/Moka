package io.github.tonnyl.moka.data

import kotlinx.datetime.Instant

data class Label(

    /**
     * Identifies the label color.
     */
    val color: String,

    /**
     * Identifies the date and time when the label was created.
     */
    val createdAt: Instant?,

    /**
     * A brief description of this label.
     */
    val description: String?,

    val id: String,

    /**
     * Indicates whether or not this is a default label.
     */
    val isDefault: Boolean,

    /**
     * Identifies the label name.
     */
    val name: String,

    /**
     * The HTTP path for this label.
     */
    val resourcePath: String,

    /**
     * Identifies the date and time when the label was last updated.
     */
    val updatedAt: Instant?,

    /**
     * The HTTP URL for this label.
     */
    val url: String

)