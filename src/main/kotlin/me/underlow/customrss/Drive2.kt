package me.underlow.customrss

import com.rometools.rome.feed.synd.SyndContentImpl
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndEntryImpl
import org.jsoup.nodes.Element
import org.slf4j.Logger


class Drive2(path: String) : WebResource {

    override val logger: Logger = loggerFor<Drive2>()
    override val url: String = "${baseUrl}/${path}"
    override val entriesSelector: String = ".l-page-columns > .g-column-mid > .js-entity"

    override fun Element.toSyndEntry(): SyndEntry? =
        kotlin.runCatching {
            val titleElement = select(".c-link--text")
            val title = titleElement[0].text()
            val url = titleElement.attr("href")
            val content = select(".c-post-preview__lead").text()
//            images don't live long enough and don't appear in rss
//            val imageDiv = select(".c-preview-pic .o-img img")
//            val image = if (imageDiv.isNotEmpty()) imageDiv[0].attr("src") else null
//            val description = image?.let { "<img src=${it}/><p>${content}</p>"; } ?: "<p>${content}</p>"

            return@runCatching SyndEntryImpl().apply {
                this.title = title
                this.description = SyndContentImpl().apply { value = "<p>${content}</p>" }
                this.link = "$baseUrl$url"
            }
        }.onFailure {
            logger.error("Cannot parse $this: $it")
        }.getOrNull()
}

private const val baseUrl = "https://www.drive2.ru"

