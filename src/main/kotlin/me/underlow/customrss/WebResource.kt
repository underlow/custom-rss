package me.underlow.customrss

import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.feed.synd.SyndFeedImpl
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.withTimeout
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.slf4j.Logger

interface WebResource {
    val logger: Logger

    val url: String
    val entriesSelector: String

    fun entries(document: Document): List<SyndEntry> {
        val items = document.select(entriesSelector)

        logger.debug("Found ${items.size} items for $url")
        return items.mapNotNull { it.toSyndEntry() }
    }

    fun Element.toSyndEntry(): SyndEntry?

    suspend fun fetch(): SyndFeed {
        logger.info("feed for $url requested")

        val html = withTimeout(10000) {
            httpClient.get<String>(url)
        }

        val document = Jsoup.parse(html)

        return SyndFeedImpl().apply {
            feedType = "rss_2.0"
            title = document.title()
            link = url
            description = "This feed has been created using custom-rss (https://github.com/underlow/custom-rss) tool"
            entries = entries(document)
        }
    }

}


private val httpClient = HttpClient()
