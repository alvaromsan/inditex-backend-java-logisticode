package com.hackathon.inditex.Repositories;

import com.hackathon.inditex.Entities.Center;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Center} entities.
 * Extends {@link JpaRepository} to provide standard database operations.
 */
public interface CenterRepository extends JpaRepository<Center, Long> {
    /**
     * Checks if a center exists at the given latitude and longitude.
     *
     * @param latitude the latitude of the center.
     * @param longitude the longitude of the center.
     * @return {@code true} if a center exists at the given coordinates, {@code false} otherwise.
     */
    boolean existsByCoordinatesLatitudeAndCoordinatesLongitude(Double latitude, Double longitude);

    /**
     * Retrieves all centers with the specified status.
     *
     * @param status the status to filter centers by.
     * @return a list of centers matching the given status; empty list if none found.
     */
    List<Center> findByStatus(String status);
}
