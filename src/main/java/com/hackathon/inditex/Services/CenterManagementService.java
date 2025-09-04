package com.hackathon.inditex.Services;

import com.hackathon.inditex.DTO.CenterRequest;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Entities.Coordinates;
import com.hackathon.inditex.Repositories.CenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Set;

/**
 * Service class responsible for managing logistics centers.
 *
 * Contains business logic for operations such as creating, updating,
 * reading and deleting centers.
 */
@Service
public class CenterManagementService {

    // Autowiring the CenterRepository bean from the ApplicationContext
    @Autowired
    private CenterRepository centerRepository;

    // Valid values for the Center#capacity attribute
    private static final Set<String> VALID_CAPACITIES = Set.of(
            "B", "M", "S", "BM", "BS", "MS", "BMS"
    );

    // Valid values for the Center#status attribute
    private static final Set<String> VALID_STATUS = Set.of(
            "AVAILABLE", "OCCUPIED"
    );

    /**
     * Creates a new logistics center based on the provided centerRequest.
     *
     * @param centerRequest the payload containing center details
     * @return success message when the center is created successfully
     * @throws ResponseStatusException if the center status or capacity is invalid (400 Bad Request)
     * @throws RuntimeException if a center already exist in the given coordinates
     * or the currentLoad exceeds maxCapacity (500 Internal Server Error)
     */
    public String createNewCenter(CenterRequest centerRequest){
        Double lat = centerRequest.getCoordinates().getLatitude();
        Double lon = centerRequest.getCoordinates().getLongitude();
        int currentLoad = centerRequest.getCurrentLoad();
        int maxCapacity = centerRequest.getMaxCapacity();

        // Verify the capacity value is valid
        if (!isValidCapacity(centerRequest.getCapacity())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid capacity value");
        }

        // Verify the status value is valid
        if (!isValidStatus(centerRequest.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid status value");
        }

        // Verify there is no center in the given coordinates
        boolean exists = centerRepository.existsByCoordinatesLatitudeAndCoordinatesLongitude(lat, lon);
        if (exists) {
            throw new RuntimeException("There is already a logistics center in that position.");
        }

        // Verify the load doesn't exceed max capacity
        if (currentLoad > maxCapacity) {
            throw new RuntimeException("Current load cannot exceed max capacity.");
        }

        // Create the new Center
        Center newCenter = new Center();
        newCenter.setName(centerRequest.getName());
        newCenter.setCapacity(centerRequest.getCapacity());
        newCenter.setStatus(centerRequest.getStatus());
        newCenter.setCurrentLoad(centerRequest.getCurrentLoad());
        newCenter.setMaxCapacity(centerRequest.getMaxCapacity());
        newCenter.setCoordinates(centerRequest.getCoordinates());

        // Save the new Center
        centerRepository.save(newCenter);

        // Return success message
        return "Logistics center created successfully.";
    }

    /**
     * Retrieves all registered logistics centers.
     *
     * @return a list with all currently registered logistics centers
     * @throws RuntimeException if no centers are registered
     */
    public List<Center> readAllCenters(){
        List<Center> centerList= centerRepository.findAll();

        // Verify the list is not empty
        if (centerList.isEmpty()) {
            throw new RuntimeException("There is no logistics center registered at this time");
        }

        // Return the list of centers
        return centerList;
    }

    /**
     * Updates an existing logistics center with the provided centerRequest data.
     *
     * @param id the ID of the center to update
     * @param centerRequest the payload containing updated center details
     * @return success message when the center is updated successfully
     * @throws ResponseStatusException if the center with the given ID is not found (404 NOT FOUND)
     * @throws ResponseStatusException if the center status or capacity is invalid (400 BAD REQUEST)
     * @throws RuntimeException if the new coordinates are already occupied by an existing center
     * or currentLoad exceeds maxCapacity (500 INTERNAL SERVER ERROR)
     */
    public String updateCenter(Long id, CenterRequest centerRequest) {

        // Object where changes will be applied
        Center center = centerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Center not found."));

        copyCenterRequestToCenter(centerRequest, center);

        centerRepository.save(center);
        // Return success message
        return "Logistics center updated successfully.";
    }

    //  Validates:
    //  - The given ID center exists --> 404 NOT FOUND

    /**
     * Deletes an existing logistics center with the provided id.
     *
     * @param id the ID of the center to delete
     * @return successful message when the center is deleted
     * @throws ResponseStatusException if the center with the given ID is not found (404 NOT FOUND)
     */
    public String deleteCenter(Long id) {
        // Object that will be deleted
        Center center = centerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Center not found."));

        centerRepository.delete(center);
        // Return success message
        return "Logistics center deleted successfully.";
    }


    /**
     * Copies non-null fields from the given CenterRequest to the target Center.
     * Performs validation on capacity, status, currentLoad, and coordinates.
     *
     * @param centerRequest the source with updated values
     * @param updatedCenter the target center to be updated
     * @throws ResponseStatusException if capacity or status is invalid (400 BAD REQUEST)
     * @throws RuntimeException if currentLoad exceeds maxCapacity or coordinates already exist (500 INTERNAL SERVER ERROR)
     */
    private void copyCenterRequestToCenter(CenterRequest centerRequest, Center updatedCenter) {
        if (centerRequest == null || updatedCenter == null) return;

        if (centerRequest.getName() != null) {
            updatedCenter.setName(centerRequest.getName());
        }
        if (centerRequest.getCapacity() != null) {
            // Verify the capacity value is valid
            if (!isValidCapacity(centerRequest.getCapacity())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid capacity value");
            }
            updatedCenter.setCapacity(centerRequest.getCapacity());
        }
        if (centerRequest.getStatus() != null) {
            // Verify the status value is valid
            if (!isValidStatus(centerRequest.getStatus())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Invalid status value");
            }
            updatedCenter.setStatus(centerRequest.getStatus());
        }


        if (centerRequest.getMaxCapacity() != null) {
            updatedCenter.setMaxCapacity(centerRequest.getMaxCapacity());
        }
        if (centerRequest.getCurrentLoad() != null) {
            updatedCenter.setCurrentLoad(centerRequest.getCurrentLoad());
        }
        // Verify currentLoad cannot exceed maxCapacity
        if (updatedCenter.getCurrentLoad() > updatedCenter.getMaxCapacity()) {
            throw new RuntimeException("Current load cannot exceed max capacity.");
        }


        // Handle coordinates - verify if the coordinates have been modified
        Coordinates newCoords = centerRequest.getCoordinates();
        Coordinates currentCoords = updatedCenter.getCoordinates();
        boolean hasCoordinatesChanged = false;

        if (newCoords != null) {
            if (newCoords.getLatitude() != null && !newCoords.getLatitude().equals(currentCoords.getLatitude())) {
                currentCoords.setLatitude(newCoords.getLatitude());
                hasCoordinatesChanged = true;
            }
            if (newCoords.getLongitude() != null && !newCoords.getLongitude().equals(currentCoords.getLongitude())) {
                currentCoords.setLongitude(newCoords.getLongitude());
                hasCoordinatesChanged = true;
            }
        }

        // If they have, check the news coordinates don't overlap with an existing center
        if (hasCoordinatesChanged) {
            boolean exists = centerRepository.existsByCoordinatesLatitudeAndCoordinatesLongitude(
                    currentCoords.getLatitude(), currentCoords.getLongitude()
            );
            if (exists) {
                throw new RuntimeException("There is already a logistics center in that position.");
            }
        }
    }

    /**
     * Checks whether the given Center capacity is valid.
     *
     * @param capacity the capacity value to validate
     * @return true if the capacity is non-null and one of the allowed values; false otherwise
     */
    private boolean isValidCapacity(String capacity) {
        return capacity != null && VALID_CAPACITIES.contains(capacity);
    }

    /**
     * Checks whether the given Center status is valid.
     *
     * @param status the status value to validate
     * @return true if the status is non-null and one of the allowed values; false otherwise
     */
    private boolean isValidStatus(String status) {
        return status != null && VALID_STATUS.contains(status);
    }
}
