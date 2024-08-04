package com.nc1_test.controller;

import com.nc1_test.service.NewsParserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/parse")
@RequiredArgsConstructor
public class ParserController {

    @Autowired
    private NewsParserService newsParserService;


    @GetMapping
    public void parser(@RequestParam String website) {
        newsParserService.parser(website);
    }

}