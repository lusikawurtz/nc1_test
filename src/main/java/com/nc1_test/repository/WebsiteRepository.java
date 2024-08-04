package com.nc1_test.repository;

import com.nc1_test.entities.ParsingRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WebsiteRepository extends JpaRepository<ParsingRule, Long> {

    ParsingRule findParsingRuleByWebsite(String website);

}