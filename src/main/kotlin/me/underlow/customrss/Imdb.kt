package me.underlow.customrss

import com.rometools.rome.feed.synd.SyndContentImpl
import com.rometools.rome.feed.synd.SyndEntry
import com.rometools.rome.feed.synd.SyndEntryImpl
import org.jsoup.nodes.Element
import org.slf4j.Logger
import java.text.SimpleDateFormat


class Imdb(userId: String) : WebResource {

    override val logger: Logger = loggerFor<Imdb>()
    override val url: String = "http://www.imdb.com/user/${userId}/ratings"
    override val entriesSelector: String = "#ratings-container > .lister-item"

    override fun Element.toSyndEntry(): SyndEntry? =
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
                this.link = url
                this.description = SyndContentImpl().apply { value = description }
                this.publishedDate = date
                this.uri = url
            }
        }.onFailure {
            me.underlow.customrss.logger.error("Cannot parse $this: $it")
        }.getOrNull()
}

private val dateFormat = SimpleDateFormat("dd MMM yyyy")
private val logger = loggerFor<Imdb>()
