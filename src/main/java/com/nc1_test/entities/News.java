package com.nc1_test.entities;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

@Data
@EqualsAndHashCode
@Table
@Entity
public class News {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String headline;
    @Column(length = 65555)
    private String description;
    @JsonSerialize(using = LocalTimeSerializer.class)
    private LocalTime publicationTime;


    public News(String headline, String description, LocalTime publicationTime) {
        this.headline = headline;
        this.description = description;
        this.publicationTime = publicationTime;
    }

    public News() {
    }

}