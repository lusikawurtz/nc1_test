package com.nc1_test.service;

import com.nc1_test.entities.Website;
import com.nc1_test.repository.WebsiteRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Log4j2
@Service
@RequiredArgsConstructor
public class SchedulerService {
    private final WebsiteRepository websiteRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    @Value("${uri}")
    private String uri;
    private static final String headerName = "X-HTTP-Method-Override";


    @Scheduled(cron = "59 59 23 * * *")
    public void deleteAllNewsBeforeTheNextDay() {
        try {
            log.info("Scheduled task: Deleting all news before the next day");
            executeDeleteNewsEndpoint();
        } catch (Exception e) {
            log.error("Error executing scheduled delete of all news: {}", e.getMessage(), e);
        }
    }

    @Scheduled(fixedRate = 1200000)
    public void parseNewsFromAllWebsites() {
        try {
            log.info("Starting scheduled parsing of news from all websites");
            parseNews();
        } catch (Exception e) {
            log.error("Error during scheduled parsing: {}", e.getMessage());
        }
    }

    private void executeDeleteNewsEndpoint() {
        HttpHeaders headers = new HttpHeaders();
        headers.add(headerName, "DELETE");
        restTemplate.exchange(uri + "news", HttpMethod.DELETE, new HttpEntity<>(headers), String.class);
    }

    private void parseNews() {
        websiteRepository.findWebsiteByParsingRulePresentIsTrue().stream()
                .map(Website::getWebsiteLink)
                .forEach(this::executeParseNewsEndpoint);
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