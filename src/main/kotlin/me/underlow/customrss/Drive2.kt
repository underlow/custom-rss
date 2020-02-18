package me.underlow.customrss

import com.rometools.rome.feed.synd.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.withTimeout
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element


object Drive2 {
    private fun buildUrl(path: String) = "${baseUrl}/experience/${path}"


    private val httpClient = HttpClient()

    fun fetchAndParse(userId: String, document: Document): List<SyndEntry> {
        val items = document.select(".l-page-columns > .g-column-mid > .js-entity")

        logger.debug("Found ${items.size} items for $userId")
        return items.mapNotNull { it.toSyndEntry() }
    }

    suspend fun fetch(path: String): SyndFeed {
        logger.info("feed for $path requested")

        val html = withTimeout(10000) {
            httpClient.get<String>(buildUrl(path))
        }

        return SyndFeedImpl().apply {
            feedType = "rss_2.0"
            title = "Drive2 page feed for ${buildUrl(path)}"
            link = buildUrl(path)
            description = "This feed has been created using custom-rss tool"
            entries = fetchAndParse(path, Jsoup.parse(html))
        }
    }
}

private fun Element.toSyndEntry(): SyndEntry? =
    kotlin.runCatching {
        val titleElement = select(".c-link--text")
        val title = titleElement.text()
        val url = titleElement.attr("href")
        val content = select(".c-post-preview__lead").text()
        val image = select(".c-preview-pic .o-img img")[0].attr("src")
        val description = "<img src=${image}/><p>${content}</p>";

        return@runCatching SyndEntryImpl().apply {
            this.title = title
            this.description = SyndContentImpl().apply { value = description }
            this.uri = url
        }
    }.onFailure {
        logger.error("Cannot parse $this: $it")
    }.getOrNull()

private const val baseUrl = "https://www.drive2.ru"

private val logger = loggerFor<Imdb>()
