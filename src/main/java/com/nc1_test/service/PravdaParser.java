package com.nc1_test.service;

import com.nc1_test.entities.News;
import com.nc1_test.entities.ParsingRule;
import com.nc1_test.repository.NewsRepository;
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
public class PravdaParser implements Parser {

    @Autowired
    private NewsRepository newsRepository;


    public void parse(ParsingRule rule) {
        String websiteName = rule.getWebsite();

        try {
            Document mainNewsPage = Jsoup.connect(websiteName + "/news/").get();
            Elements newsList = mainNewsPage.getElementsByClass("article_header");

            for (Element news : newsList) {
                LocalTime publicationTime;
                Elements timeElement = ((Element) news.parentNode().parentNode()).getElementsByClass("article_time");
                if (!timeElement.isEmpty()) {
                    String time = timeElement.get(0).text();
                    publicationTime = LocalTime.parse(time, DateTimeFormatter.ofPattern("H:mm"));
                } else {
                    publicationTime = null;
                }

                List<Node> titleElements = news.child(0).childNodes();
                String headline;
                if (titleElements.size() == 2) {
                    if (publicationTime != null)
                        headline = ((TextNode) titleElements.get(1)).getWholeText();
                    else
                        headline = news.child(0).child(1).text();
                } else
                    headline = news.text();


                Elements linkElement = news.getElementsByTag("a");
                if (linkElement.isEmpty())
                    continue;
                String link = linkElement.get(0).attribute("href").getValue();
                String description;
                Document newsPage;
                Elements elementsWithText;
                StringBuilder newsText = new StringBuilder();

                if (!link.startsWith("http")) {
                    newsPage = Jsoup.connect(websiteName + link).get();
                    elementsWithText = newsPage.getElementsByClass("post_text");
                } else {
                    newsPage = Jsoup.connect(link).userAgent("Mozilla").get();
                    if (link.contains("eurointegration")) {
                        elementsWithText = newsPage.getElementsByClass("post__text");
                    } else if (link.contains("mezha.media")) {
                        elementsWithText = newsPage.getElementsByClass("post-content clearfix");
                    } else {
                        elementsWithText = newsPage.getElementsByClass("post_news_text");
                    }
                }
                if (elementsWithText.isEmpty())
                    continue;
                elementsWithText = elementsWithText.get(0).getElementsByTag("p");

                for (Element text : elementsWithText) {
                    newsText.append(text.text());
                }
                description = newsText.toString();
                log.info("");
                log.info("Title: " + headline);
                log.info("Publication time: " + publicationTime);
                log.info("Description: " + description);
                log.info("");
                addNewsToRepositoryIfNotExist(headline, publicationTime, description);
            }
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    private void addNewsToRepositoryIfNotExist(String headline, LocalTime publicationTime, String description) {
        News newNews = new News(headline, description, publicationTime);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnorePaths("id") // Ignore the ID field
                .withMatcher("headline", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("description", ExampleMatcher.GenericPropertyMatchers.exact())
                .withMatcher("publicationTime", ExampleMatcher.GenericPropertyMatchers.exact());

        Example<News> example = Example.of(newNews, matcher);
        if (!newsRepository.exists(example))
            newsRepository.save(newNews);
    }

}