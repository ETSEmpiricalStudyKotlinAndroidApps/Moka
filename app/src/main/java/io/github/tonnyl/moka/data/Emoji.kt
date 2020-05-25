package io.github.tonnyl.moka.data

interface EmojiType

data class Emoji(
    val emoji: String,
    val names: List<String>,
    val tags: List<String>,
    val description: String,
    val category: String
) : EmojiType

enum class EmojiCategory(val categoryValue: String) : EmojiType {

    /**
     * local category.
     */
    RecentlyUsed("Recently Used"),

    SmileysAndEmotion("Smileys & Emotion"),

    PeopleAndBody("People & Body"),

    AnimalsAndNature("Animals & Nature"),

    FoodAndDrink("Food & Drink"),

    TravelAndPlaces("Travel & Places"),

    Activities("Activities"),

    Objects("Objects"),

    Symbols("Symbols"),

    Flags("Flags"),

    GitHubCustomEmoji("GitHub Custom Emoji"),

}

data class SearchableEmoji(
    val emoji: String,
    val name: String,
    val category: String
)