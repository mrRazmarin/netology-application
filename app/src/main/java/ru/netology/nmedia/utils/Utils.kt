package ru.netology.nmedia.utils


fun converterCountChoice(count: Long): String {
    return when {
        count >= 1_000_000 -> convertMillions(count)
        count >= 10_000    -> "${count / 1_000}K"
        count >= 1_000     -> convertThousands(count)
        else               -> count.toString()
    }
}

private fun convertThousands(count: Long): String {
    val hundreds = count / 100

    val intPart = hundreds / 10
    val fracPart = hundreds % 10

    return if (fracPart > 0) "$intPart.${fracPart}K" else "${intPart}K"
}

private fun convertMillions(count: Long): String {
    val hundredThousands = count / 100_000

    val intPart = hundredThousands / 10
    val fracPart = hundredThousands % 10

    return if (fracPart > 0) "$intPart.${fracPart}M" else "${intPart}M"
}