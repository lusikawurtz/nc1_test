package com.nc1_test.service;

import com.nc1_test.entities.News;
import com.nc1_test.entities.NewsTime;
import com.nc1_test.repository.NewsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;


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
        }
    }

    public News addNews(News news) {
        return newsRepository.save(news);
    }

    public News updateNews(Long id, News newsWithNewValues) {
        News newsWithOldValues = newsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("News not found with id: " + id));
        updateNewsValues(newsWithOldValues, newsWithNewValues);
        return newsRepository.save(newsWithOldValues);
    }

    public void deleteNews(Long id) {
        News newsById = newsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("News not found with id: " + id));
        newsRepository.delete(newsById);
    }

    public void deleteAllNews() {
        newsRepository.deleteAll();
    }

    private void updateNewsValues(News existingNews, News newNews) {
        existingNews.setHeadline(newNews.getHeadline());
        existingNews.setDescription(newNews.getDescription());
        existingNews.setPublicationTime(newNews.getPublicationTime());
    }

}