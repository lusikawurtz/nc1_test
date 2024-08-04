package com.nc1_test.service;

import com.nc1_test.entities.Website;
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
    private WebsiteRepository websiteRepository;
    @Autowired
    private PravdaParser pravdaParser;
    RestTemplate restTemplate = new RestTemplate();
    @Value("${uri}")
    private String uri;


    public void parser(String website) {
        Website websiteByWebsite = websiteRepository.findWebsiteByWebsiteName(website);
        if (websiteByWebsite == null) {
            addNewWebsite(website);
            return;
        }

        if (!websiteByWebsite.isHasParsingRule())
            return;

        String websiteName = websiteByWebsite.getWebsiteName();
        if (websiteName.equals("https://www.pravda.com.ua")) {
            pravdaParser.parse(websiteByWebsite);
        }
    }

    public void parseNews() {
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
        restTemplate.getForObject(targetUrl, String.class);
    }

    private void addNewWebsite(String website) {
        log.warn("we dont have such site");
        websiteRepository.save(new Website(website));
    }

}