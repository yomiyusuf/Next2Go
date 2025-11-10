package com.yomi.next2go.core.domain.model

enum class CategoryId(
    val id: String,
    val emoji: String,
    val categoryColor: CategoryColor
) {
    GREYHOUND(
        id = "9daef0d7-bf3c-4f50-921d-8e818c60fe61",
        emoji = "\uD83E\uDDAE",
        categoryColor = CategoryColor.RED
    ),
    HARNESS(
        id = "161d9be2-e909-4326-8c2c-35ed71fb460b",
        emoji = "\uD83D\uDE83",
        categoryColor = CategoryColor.YELLOW
    ),
    HORSE(
        id = "4a2788f8-e825-4d36-9894-efd4baf1cfae",
        emoji = "\uD83C\uDFC7",
        categoryColor = CategoryColor.GREEN
    ),
    ;

    companion object {
        fun fromId(id: String): CategoryId? {
            return entries.find { it.id == id }
        }
    }
}
