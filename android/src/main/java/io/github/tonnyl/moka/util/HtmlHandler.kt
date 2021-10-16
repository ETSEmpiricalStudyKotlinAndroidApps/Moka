package io.github.tonnyl.moka.util

import android.net.Uri
import org.commonmark.ext.autolink.AutolinkExtension
import org.commonmark.ext.front.matter.YamlFrontMatterExtension
import org.commonmark.ext.gfm.strikethrough.StrikethroughExtension
import org.commonmark.ext.gfm.tables.TablesExtension
import org.commonmark.ext.ins.InsExtension
import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer
import org.jsoup.Jsoup

object HtmlHandler {

    private val extensions = listOf(
        TablesExtension.create(),
        AutolinkExtension.create(),
        StrikethroughExtension.create(),
        InsExtension.create(),
        YamlFrontMatterExtension.create()
    )
    private val parser = Parser.builder()
        .extensions(extensions)
        .build()

    private val renderer = HtmlRenderer.builder()
        .extensions(extensions)
        .escapeHtml(false)
        .build()

    fun toHtml(
        rawText: String,
        login: String,
        repositoryName: String,
        branch: String = "master"
    ): String {
        val document = parser.parse(rawText)
        return wrapWithHtmlTemplate(renderer.render(document), login, repositoryName, branch)
    }

    fun basicHtmlTemplate(
        cssPath: String,
        body: String
    ): String {
        return """
            |<html>
            |<head>
            |<meta charset="UTF-8">
            |<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=0"/>
            |<link rel="stylesheet" type="text/css" href=$cssPath>
            |<script src="./intercept-hash.js"></script>
            |</head>
            |<body>
            |$body
            |<script src="./intercept-touch.js"></script>
            |</body>
            |</html>
            """.trimMargin()
    }

    private fun formatHtmlWithLink(
        raw: String,
        login: String,
        repositoryName: String,
        branch: String
    ): String {
        val document = Jsoup.parse(raw, "")
        val imageElements = document.getElementsByTag("img")
        if (!imageElements.isNullOrEmpty()) {
            for (element in imageElements) {
                var src = element.attr("src")
                if (src.length >= 2
                    && src.startsWith(".") // relative path
                ) {
                    src = src.substring(1, src.length)
                    val finalSrc =
                        "https://raw.githubusercontent.com/$login/$repositoryName/$branch$src"
                    element.attr("src", finalSrc)
                } else if (src.startsWith("http://github.com")
                    || src.startsWith("https://github.com")
                ) {
                    // https://github.com/TonnyL/PaperPlane/blob/master/art/sreenshot.png
                    val url = Uri.parse(src)
                    val paths = url.pathSegments.toMutableList()
                    val builder = StringBuilder()

                    paths.removeAt(0) // remove owner's name
                    paths.removeAt(1) // remove repository name
                    paths.remove("blob")

                    paths.add(0, login)
                    paths.add(1, repositoryName)

                    builder.append(paths.joinToString("/"))

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
                    || href.startsWith("mailto:")
                ) {
                    continue
                }
            }
        }
        return document.html()
    }

    private fun wrapWithHtmlTemplate(
        raw: String,
        login: String,
        repositoryName: String,
        branch: String
    ): String {
        return basicHtmlTemplate(
            cssPath = "./github_light.css",
            body = formatHtmlWithLink(raw, login, repositoryName, branch)
        )
    }

}