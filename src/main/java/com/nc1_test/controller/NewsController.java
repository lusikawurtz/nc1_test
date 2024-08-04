package com.nc1_test.controller;

import com.nc1_test.entities.News;
import com.nc1_test.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private NewsService newsService;


    @GetMapping
    public ResponseEntity<List<News>> getNews(@RequestParam("time") String time) {
        try {
            log.info("Getting the news from " + time + ": start");
            List<News> news = newsService.getAllNewsByTime(time);
            log.info("Getting the news from " + time + ": success");
            return ResponseEntity.ok().body(news);
        } catch (Exception e) {
            log.info("Getting the news from " + time + ": error");
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @PostMapping
    public News addNews(@RequestBody News news) {
        return newsService.addNews(news);
    }

    @PutMapping("/{id}")
    public News updateNews(@PathVariable Long id, @RequestBody News news) {
        return newsService.updateNews(id, news);
    }

    @DeleteMapping("/{id}")
    public void deleteNews(@PathVariable Long id) {
        newsService.deleteNews(id);
    }

    @DeleteMapping()
    public ResponseEntity<String> deleteAllNews() {
        try {
            log.info("Deleting all news for " + LocalDate.now() + ": start");
            newsService.deleteNews();
            log.info("Deleting all news for " + LocalDate.now() + ": success");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("Deleting all news for " + LocalDate.now() + ": error");
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @Scheduled(cron = "59 59 23 * * * ")
    private void deleteAllNewsBeforeTheNextDay() {
        newsService.executeDeleteNewsEndpoint();
    }

}