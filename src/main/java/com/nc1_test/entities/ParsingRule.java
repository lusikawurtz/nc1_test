package com.nc1_test.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table
@Entity
public class ParsingRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String website;
    private boolean hasParsingRule;


    public ParsingRule(String website) {
        this.website = website;
        this.hasParsingRule = false;
    }

    public ParsingRule() {
    }

}