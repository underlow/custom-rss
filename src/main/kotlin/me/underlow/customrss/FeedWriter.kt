package me.underlow.customrss

import com.rometools.rome.feed.synd.SyndFeed
import com.rometools.rome.io.SyndFeedOutput
import java.io.StringWriter


fun writeFeedXml(feed: SyndFeed): String {
    val writer = StringWriter()
    val output = SyndFeedOutput()
    output.output(feed, writer)
    return writer.toString()
}
