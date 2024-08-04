package com.nc1_test.service;

import com.nc1_test.entities.ParsingRule;
import com.nc1_test.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class NewsParserService {

    @Autowired
    private WebsiteRepository websiteRepository;
    @Autowired
    private PravdaParser pravdaParser;


    public void parser(String website) {
        ParsingRule parsingRuleByWebsite = websiteRepository.findParsingRuleByWebsite(website);
        if (parsingRuleByWebsite == null) {
            addNewWebsite(website);
            return;
        }

        if (!parsingRuleByWebsite.isHasParsingRule())
            return;

        String websiteName = parsingRuleByWebsite.getWebsite();
        if (websiteName.equals("https://www.pravda.com.ua")) {
            pravdaParser.parse(parsingRuleByWebsite);
        }
    }

    private void addNewWebsite(String website) {
        log.warn("we dont have such site");
        websiteRepository.save(new ParsingRule(website));
    }

}