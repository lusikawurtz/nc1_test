package com.nc1_test.repository;

import com.nc1_test.entities.Website;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WebsiteRepository extends JpaRepository<Website, Long> {

    Website findWebsiteByWebsiteName(String websiteName);

    List<Website> findWebsiteByHasParsingRuleIsTrue();

}