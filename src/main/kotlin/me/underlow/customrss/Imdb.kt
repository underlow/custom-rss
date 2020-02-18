package me.underlow.customrss

import com.rometools.rome.feed.synd.*
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import kotlinx.coroutines.withTimeout
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.text.SimpleDateFormat


object Imdb {
    private fun buildUrl(userId: String) = "http://www.imdb.com/user/${userId}/ratings"

    private val httpClient = HttpClient()

    fun fetchAndParse(userId: String, document: Document): List<SyndEntry> {
        val items = document.select("#ratings-container > .lister-item")

        logger.debug("Found ${items.size} items for $userId")

        return items.mapNotNull { it.toSyndEntry() }
    }

    suspend fun fetch(userId: String): SyndFeed {
        logger.info("feed for $userId requested")

        val html = withTimeout(10000) {
            httpClient.get<String>(buildUrl(userId))
        }

        return SyndFeedImpl().apply {
            feedType = "rss_2.0"
            title = "IMDB rating feed for $userId"
            link = buildUrl(userId)
            description = "This feed has been created using custom-rss tool"
            entries = fetchAndParse(userId, Jsoup.parse(html))
        }
    }
}

private fun Element.toSyndEntry(): SyndEntry? =
    kotlin.runCatching {
        val titleElement = select(".lister-item-header a")
        val rating = select(".ipl-rating-star--other-user .ipl-rating-star__rating").text()
        val title = "Rated ${titleElement.text()} ${rating}/10"
        val url = "http://www.imdb.com${titleElement.attr("href")}"
        val content = select(".lister-item-content p")
        val dateString = content[1].text().replace("Rated on", "").trim();
        val date = dateFormat.parse(dateString)
        val text = content[2].text();
        val image = select(".lister-item-image img").attr("loadlate");
        val description = "<img src=${image}/><p>${text}</p>";

        return@runCatching SyndEntryImpl().apply {
            this.title = title
//            this.link = link
            this.description = SyndContentImpl().apply { value = description }
            this.publishedDate = date
            this.uri = url
        }
    }.onFailure {
        logger.error("Cannot parse $this: $it")
    }.getOrNull()

private val dateFormat = SimpleDateFormat("dd MMM yyyy")
private val logger = loggerFor<Imdb>()
