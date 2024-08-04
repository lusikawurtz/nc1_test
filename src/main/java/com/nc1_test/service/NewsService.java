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
            NewsTime newsTime = NewsTime.valueOf(time);
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
        } catch (Exception e) {
            log.error(e.getMessage());
            throw new RuntimeException("Wrong input");
        }
    }

    public News addNews(News news) {
        return newsRepository.save(news);
    }

    public News updateNews(Long id, News newsWithNewValues) {
        News newsWithOldValues = newsRepository.findById(id).orElseThrow();
        setNewValues(newsWithNewValues, newsWithOldValues);
        return newsRepository.save(newsWithOldValues);
    }

    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    public void deleteNews() {
        newsRepository.deleteAll();
    }

    public void executeDeleteNewsEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-HTTP-Method-Override", "DELETE");
        restTemplate.exchange(uri + "news", HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
    }

    private void setNewValues(News newsWithNewValues, News newsWithOldValues) {
        newsWithOldValues.setHeadline(newsWithNewValues.getHeadline());
        newsWithOldValues.setDescription(newsWithNewValues.getDescription());
        newsWithOldValues.setPublicationTime(newsWithNewValues.getPublicationTime());
    }

}