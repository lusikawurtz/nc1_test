package com.nc1_test.controller;

import com.nc1_test.service.ParserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseEntity<String> parser(@RequestParam("website") String website) {
        try {
            log.info("Parse all news for " + LocalDate.now() + " for " + website + ": start");
            parserService.parser(website);
            log.info("Parse all news for " + LocalDate.now() + " for " + website + ": success");
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.info("Parse all news for " + LocalDate.now() + " for " + website + ": error");
            log.error(e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @Scheduled(fixedRate = 1200000)
    private void parseNews() {
        parserService.parseNews();
    }


}