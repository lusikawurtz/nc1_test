package com.nc1_test.controller;

import com.nc1_test.service.ParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@Log4j2
@RestController
@RequestMapping("/parse")
@RequiredArgsConstructor
public class ParserController {

    @Autowired
    private ParserService parserService;


    @GetMapping
    public ResponseEntity<String> parseAllNewsForAWebsite(@RequestParam("website") String website) {
        log.info("Starting to parse news for website: {} on {}", website, LocalDate.now());
        try {
            parserService.parser(website);
            log.info("Parse news for website: {} on {}: successful", website, LocalDate.now());
            return ResponseEntity.ok("Parsing successful");
        } catch (IllegalArgumentException e) {
            log.error("Invalid website input: {}", website, e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        } catch (Exception e) {
            log.error("Error parsing news for website: {} on {}: {}", website, LocalDate.now(), e.getMessage());
            return ResponseEntity.status(500).body("Parsing failed");
        }
    }

    @Scheduled(fixedRate = 1200000)
    public void parseNewsFromAllWebsites() {
        try {
            log.info("Starting scheduled parsing of news from all websites");
            parserService.parseNewsFromAllWebsites();
        } catch (Exception e) {
            log.error("Error during scheduled parsing: {}", e.getMessage());
        }
    }

}