package com.nc1_test.repository;

import com.nc1_test.entities.News;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalTime;
import java.util.List;

public interface NewsRepository extends JpaRepository<News, Long> {

    List<News> findByPublicationTimeBetween(LocalTime publicationTimeStart, LocalTime publicationTimeEnd);

}