package com.nc1_test.entities;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Table
@Entity
public class Website {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String websiteLink;
    private boolean parsingRulePresent;


    public Website(String websiteLink) {
        this.websiteLink = websiteLink;
        this.parsingRulePresent = false;
    }

    public Website() {
    }

}