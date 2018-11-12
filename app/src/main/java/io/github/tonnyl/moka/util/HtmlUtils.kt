package io.github.tonnyl.moka.util

import android.net.Uri
import org.jsoup.Jsoup

fun wrapWithHtmlTemplate(
        raw: String,
        login: String,
        repositoryName: String,
        expression: String
): String {
    val strings = expression.split(":")
    return if (strings.size >= 2) {
        """
        |<html>
        |<head>
        |<meta charset="UTF-8">
        |<meta name="viewport" content="width=device-width; initial-scale=1.0; maximum-scale=1.0; user-scalable=0;"/>
        |<link rel="stylesheet" type="text/css" href=${"./github.css"}>
        |<script src="./intercept-hash.js"></script>
        |</head>
        |<body>
        |${formatHtmlWithLink(raw, login, repositoryName, strings.first())}
        |<script src="./intercept-touch.js"></script>
        |</body>
        |</html>
    """.trimMargin()
    } else ""
}

private fun formatHtmlWithLink(
        raw: String,
        login: String,
        repositoryName: String,
        branch: String
): String {
    val document = Jsoup.parse(raw, "")
    val imageElements = document.getElementsByTag("img")
    if (imageElements != null && imageElements.isNotEmpty()) {
        for (element in imageElements) {
            var src = element.attr("src")
            if (src.startsWith(".") && src.length >= 2) {
                src = src.substring(1, src.length)
                val finalSrc = "https://raw.githubusercontent.com/$login/$repositoryName/$branch$src"
                element.attr("src", finalSrc)
            } else if (src.startsWith("http://github.com") || src.startsWith("https://github.com")) {
                // https://github.com/TonnyL/PaperPlane/blob/master/art/sreenshot.png
                val url = Uri.parse(src)
                val paths = url.pathSegments
                val builder = StringBuilder()

                paths.removeAt(0) // remove owner's name
                paths.removeAt(1) // remove repository name
                if (paths.contains("blob")) {
                    paths.remove("blob")
                }

                paths.add(0, login)
                paths.add(1, repositoryName)

                paths.forEach { path ->
                    builder.append(path).append("/")
                }
                builder.removeRange(builder.length - 1, builder.length)
                element.attr("src", "https://raw.githubusercontent.com/$builder")
            }
        }
    }
    val linkElements = document.getElementsByTag("a")
    if (linkElements != null && linkElements.isNotEmpty()) {
        for (element in linkElements) {
            val href = element.attr("href")
            if (href.startsWith("#")
                    || href.startsWith("http://")
                    || href.startsWith("https://")
                    || href.startsWith("mailto:")) {
                continue
            }
        }
    }
    return document.html()
}