package com.nc1_test.service;

import com.nc1_test.entities.News;
import com.nc1_test.entities.NewsTime;
import com.nc1_test.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class NewsService {

    @Autowired
    private NewsRepository newsRepository;
    RestTemplate restTemplate = new RestTemplate();
    @Value("${uri}")
    private String uri;


    public List<News> getAllNewsByTime(String time) {
        try {
            NewsTime newsTime = NewsTime.valueOf(time.toUpperCase());
            switch (newsTime) {
                case MORNING -> {
                    return newsRepository.findByPublicationTimeBetween(LocalTime.of(0, 0), LocalTime.of(7, 59));
                }
                case DAY -> {
                    return newsRepository.findByPublicationTimeBetween(LocalTime.of(8, 0), LocalTime.of(15, 59));
                }
                case EVENING -> {
                    return newsRepository.findByPublicationTimeBetween(LocalTime.of(16, 0), LocalTime.of(23, 59));
                }
                default -> {
                    return newsRepository.findAll();
                }
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid time input: {}", time, e);
            throw new IllegalArgumentException("Invalid input for news time", e);
        } catch (Exception e) {
            log.error("Error fetching news by time: {}", time, e);
            throw new RuntimeException("Error fetching news by time", e);
        }
    }

    public News addNews(News news) {
        return newsRepository.save(news);
    }

    public News updateNews(Long id, News newsWithNewValues) {
        News newsWithOldValues = newsRepository.findById(id).orElseThrow(() ->
                new RuntimeException("News not found with id: " + id));
        updateNewsValues(newsWithOldValues, newsWithNewValues);
        return newsRepository.save(newsWithOldValues);
    }

    public void deleteNews(Long id) {
        if (newsRepository.existsById(id)) {
            newsRepository.deleteById(id);
        } else {
            throw new RuntimeException("News not found with id: " + id);
        }
    }

    public void deleteAllNews() {
        newsRepository.deleteAll();
    }

    public void executeDeleteNewsEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-HTTP-Method-Override", "DELETE");
        restTemplate.exchange(uri + "news", HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
    }

    private void updateNewsValues(News existingNews, News newNews) {
        existingNews.setHeadline(newNews.getHeadline());
        existingNews.setDescription(newNews.getDescription());
        existingNews.setPublicationTime(newNews.getPublicationTime());
    }

}