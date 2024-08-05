package com.nc1_test.service;

import com.nc1_test.entities.Website;
import com.nc1_test.entities.WebsiteData;
import com.nc1_test.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ParserService {

    @Autowired
    private final WebsiteRepository websiteRepository;
    @Autowired
    private final PravdaParser pravdaParser;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${uri}")
    private String uri;


    public void parser(String websiteName) {
        Website website = websiteRepository.findWebsiteByWebsiteName(websiteName);
        if (website == null) {
            log.warn("No website with name '{}' in the database. Adding it for future parsing rule.", websiteName);
            addNewWebsite(websiteName);
            return;
        }

        if (!website.isHasParsingRule()) {
            log.warn("No parsing rule for the website '{}'. Please add a parsing rule.", websiteName);
            return;
        }

        try {
            WebsiteData websiteData = WebsiteData.getByWebsiteLink(websiteName);
            if (websiteData == null) {
                log.error("No parser available for website '{}'.", websiteName);
                return;
            }

            switch (websiteData) {
                case PRAVDA -> pravdaParser.parseNews();
                default -> log.warn("No parser available for website '{}'.", websiteName);
            }
        } catch (IllegalArgumentException e) {
            log.error("Invalid website name '{}' provided.", websiteName, e);
            throw new IllegalArgumentException("Invalid input for news time", e);
        } catch (Exception e) {
            log.error("Error fetching news by website '{}': {}", websiteName, e);
            throw new RuntimeException("Error fetching news by website {}", e);
        }
    }

    private void addNewWebsite(String websiteName) {
        log.info("Adding new website '{}' to the repository.", websiteName);
        Website website = new Website(websiteName);
        website.setHasParsingRule(false);
        websiteRepository.save(website);
    }

    public void parseNewsFromAllWebsites() {
        List<Website> websitesForParsing = websiteRepository.findWebsiteByHasParsingRuleIsTrue();
        List<String> websitesNames = websitesForParsing.stream().map(Website::getWebsiteName).toList();

        for (String websiteName : websitesNames) {
            executeParseNewsEndpoint(websiteName);
        }
    }

    private void executeParseNewsEndpoint(String websiteName) {
        URI targetUrl = UriComponentsBuilder.fromUriString(uri + "parse")
                .queryParam("website", websiteName)
                .build()
                .encode()
                .toUri();
        try {
            restTemplate.getForObject(targetUrl, String.class);
        } catch (Exception e) {
            log.error("Error executing parse news endpoint for website '{}'.", websiteName, e);
        }
    }

}