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
    private String websiteName;
    private boolean hasParsingRule;


    public Website(String websiteName) {
        this.websiteName = websiteName;
        this.hasParsingRule = false;
    }

    public Website() {
    }

}