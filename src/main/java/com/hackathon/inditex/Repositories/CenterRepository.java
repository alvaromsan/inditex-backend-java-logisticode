package com.hackathon.inditex.Repositories;

import com.hackathon.inditex.Entities.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long> {
    boolean existsByCoordinatesLatitudeAndCoordinatesLongitude(Double latitude, Double longitude);
    List<Center> findByStatus(String status);
}
