package com.nc1_test.service;

import com.nc1_test.entities.News;
import com.nc1_test.repository.NewsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {
    private NewsRepository newsRepository;


    public List<News> getAllNews() {
        return newsRepository.findAll();
    }

    public News addNews(News news) {
        return newsRepository.save(news);
    }

    public News updateNews(Long id, News news) {
        News existingNews = newsRepository.findById(id).orElseThrow();
        existingNews.setHeadline(news.getHeadline());
        existingNews.setDescription(news.getDescription());
        existingNews.setPublicationTime(news.getPublicationTime());
        return newsRepository.save(existingNews);
    }

    public void deleteNews(Long id) {
        newsRepository.deleteById(id);
    }

    @Scheduled(fixedRate = 1200000) // 20 minutes
    public void parseNews() {
    }

}