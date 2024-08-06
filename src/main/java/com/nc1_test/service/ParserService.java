package com.nc1_test.service;

import com.nc1_test.entities.Website;
import com.nc1_test.entities.WebsiteData;
import com.nc1_test.parser.PravdaParser;
import com.nc1_test.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class ParserService {

    private final WebsiteRepository websiteRepository;
    private final PravdaParser pravdaParser;


    public void parse(String websiteLink) {
        Website website = websiteRepository.findWebsiteByWebsiteLink(websiteLink);
        if (noWebsiteWithName(websiteLink, website)) return;
        if (noParsingRuleForWebsite(websiteLink, website)) return;

        try {
            WebsiteData websiteData = WebsiteData.getByWebsiteLink(websiteLink);

            switch (websiteData) {
                case PRAVDA -> pravdaParser.parseNews();
                default -> log.warn("No parser available for website '{}'.", websiteLink);
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid website name '{}' provided.", websiteLink, e);
            throw new IllegalArgumentException("Invalid input for news time", e);
        } catch (Exception e) {
            log.error("Error fetching news by website '{}': {}", websiteLink, e);
            throw new RuntimeException("Error fetching news by website {}", e);
        }
    }

    private boolean noParsingRuleForWebsite(String websiteLink, Website website) {
        if (!website.isParsingRulePresent()) {
            log.warn("No parsing rule for the website '{}'. Please add a parsing rule.", websiteLink);
            return true;
        }
        return false;
    }

    private boolean noWebsiteWithName(String websiteName, Website website) {
        if (website == null) {
            log.warn("No website with name '{}' in the database. Adding it for future parsing rule.", websiteName);
            addNewWebsite(websiteName);
            return true;
        }
        return false;
    }

    private void addNewWebsite(String websiteName) {
        log.info("Adding new website '{}' to the repository.", websiteName);
        Website website = new Website(websiteName);
        website.setParsingRulePresent(false);
        websiteRepository.save(website);
    }

}