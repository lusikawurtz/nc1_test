package com.nc1_test.controller;

import com.nc1_test.entities.News;
import com.nc1_test.service.NewsService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Log4j2
@RestController
@RequestMapping("/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;


    @GetMapping
    public ResponseEntity<List<News>> getNews(@RequestParam("time") String time) {
        try {
            log.info("Getting the news for time: {}", time);
            List<News> news = newsService.getAllNewsByTime(time);
            return ResponseEntity.ok(news);
        } catch (IllegalArgumentException e) {
            log.error("Invalid time input: {}", time, e);
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            log.error("Error getting the news for time: {}", time, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping
    public ResponseEntity<News> addNews(@RequestBody News news) {
        try {
            News createdNews = newsService.addNews(news);
            return ResponseEntity.ok(createdNews);
        } catch (Exception e) {
            log.error("Error adding news: {}", news, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<News> updateNews(@PathVariable Long id, @RequestBody News news) {
        try {
            News updatedNews = newsService.updateNews(id, news);
            return ResponseEntity.ok(updatedNews);
        } catch (EntityNotFoundException e) {
            log.error("Error deleting news with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error updating news with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNews(@PathVariable Long id) {
        try {
            newsService.deleteNews(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            log.error("Error deleting news with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error deleting news with id {}: {}", id, e.getMessage(), e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllNews() {
        try {
            log.info("Deleting all news for {}: start", LocalDate.now());
            newsService.deleteAllNews();
            log.info("Deleting all news for {}: success", LocalDate.now());
            return ResponseEntity.ok("All news deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting all news: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body("Error deleting all news");
        }
    }

}