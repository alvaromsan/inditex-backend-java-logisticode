package com.hackathon.inditex.Repositories;

import com.hackathon.inditex.Entities.Center;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CenterRepository extends JpaRepository<Center, Long> {
    boolean existsByCoordinatesLatitudeAndCoordinatesLongitude(Double latitude, Double longitude);
}
