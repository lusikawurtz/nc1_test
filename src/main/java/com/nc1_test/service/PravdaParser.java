package com.nc1_test.service;

import com.nc1_test.entities.News;
import com.nc1_test.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@Log4j2
@Service
@RequiredArgsConstructor
public class PravdaParser implements Parser {

    @Autowired
    private NewsRepository newsRepository;
    private static final String WEBSITE_URL = "https://www.pravda.com.ua";
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("H:mm");


    public void parseNews() {
        try {
            Document mainNewsPage = Jsoup.connect(WEBSITE_URL + "/news/").get();
            Elements newsList = mainNewsPage.getElementsByClass("article_header");

            for (Element news : newsList) {
                News newNews = extractNewsData(news);
                saveIfNotExist(newNews);
            }
        } catch (IOException e) {
            log.error("Error fetching or parsing news: {}", e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error: {}", e.getMessage(), e);
        }
    }

    private News extractNewsData(Element news) throws IOException {
        LocalTime publicationTime = extractPublicationTime(news);
        String headline = extractHeadline(news, publicationTime);
        String description = extractDescription(news);
        return new News(headline, description, publicationTime);
    }

    private String extractDescription(Element news) {
        Elements elementWithNewsLink = news.getElementsByTag("a");
        if (elementWithNewsLink.isEmpty())
            return null;

        Elements elementsWithText = getElementsWithText(elementWithNewsLink);
        if (elementsWithText == null)
            return null;

        StringBuilder description = new StringBuilder();
        for (Element text : elementsWithText) {
            description.append(" ").append(text.text());
        }
        return description.toString();
    }

    private Elements getElementsWithText(Elements linkElement) {
        String link = linkElement.attr("href");
        Document newsPage;
        Elements elementsWithText;

        try {
            if (!link.startsWith("http")) {
                newsPage = Jsoup.connect(WEBSITE_URL + link).get();
            } else {
                newsPage = Jsoup.connect(link).userAgent("Mozilla").get();
            }
            elementsWithText = newsPage.getElementsByClass("post__text");
            elementsWithText.addAll(newsPage.getElementsByClass("post_text"));
            elementsWithText.addAll(newsPage.getElementsByClass("post-content clearfix"));
            elementsWithText.addAll(newsPage.getElementsByClass("post_news_text"));
            elementsWithText.addAll(newsPage.getElementsByClass("post_article_text"));
        } catch (Exception e) {
            log.error("Error fetching or parsing news page: {}", e.getMessage(), e);
            return null;
        }

        if (elementsWithText.isEmpty()) {
            return null;
        }
        return elementsWithText.get(0).getElementsByTag("p");
    }

    private String extractHeadline(Element news, LocalTime publicationTime) {
        List<Node> titleElements = news.child(0).childNodes();
        if (titleElements.size() == 2) {
            if (publicationTime != null)
                return ((TextNode) titleElements.get(1)).getWholeText().trim();
            else
                return ((Element) titleElements.get(1)).text();
        } else {
            return news.text().trim();
        }
    }

    private LocalTime extractPublicationTime(Element news) {
        Elements timeElement = ((Element) news.parentNode().parentNode()).getElementsByClass("article_time");
        if (!timeElement.isEmpty()) {
            String time = timeElement.get(0).text();
            return LocalTime.parse(time, TIME_FORMATTER);
        }
        return null;
    }

    private void saveIfNotExist(News news) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id")
                .withMatcher("headline", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("publicationTime", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<News> example = Example.of(news, matcher);
        if (!newsRepository.exists(example)) {
//                log.info("");
//                log.info("Title: " + headline);
//                log.info("Publication time: " + publicationTime);
//                log.info("Description: " + description);
//                log.info("");
            newsRepository.save(news);
        }
    }

}