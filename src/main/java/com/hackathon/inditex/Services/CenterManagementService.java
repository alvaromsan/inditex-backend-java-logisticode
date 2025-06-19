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

@Service
public class CenterManagementService {
    @Autowired
    private CenterRepository centerRepository;

    private static final Set<String> VALID_CAPACITIES = Set.of(
            "B", "M", "S", "BM", "BS", "MS", "BMS"
    );

    private static final Set<String> VALID_STATUS = Set.of(
            "AVAILABLE", "OCCUPIED"
    );

    //  Validates:
    //  1) Center status is valid --> 400 BAD REQUEST
    //  2) Center capacity is valid --> 400 BAD REQUEST
    //  3) Given coordinates don't belong to an already registered center --> 500 INTERNAL SERVER ERROR
    //  4) currentLoad doesn't exceed maxCapacity --> 500 INTERNAL SERVER ERROR
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

    public List<Center> readAllCenters(){
        // Verify there is no center in the given coordinates
        List<Center> centerList= centerRepository.findAll();
        if (centerList.isEmpty()) {
            throw new RuntimeException("There is no logistics center registered at this time");
        }

        // Return the list of centers
        return centerList;
    }

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

    public String deleteCenter(Long id) {
        // Object that will be deleted
        Center center = centerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Center not found."));

        centerRepository.delete(center);
        // Return success message
        return "Logistics center deleted successfully.";
    }


    //  Method that copies the values from the request into a Center-type Object
    //  Validates:
    //  1) Center status is valid --> 400 BAD REQUEST
    //  2) Center capacity is valid --> 400 BAD REQUEST
    //  3) Given coordinates don't belong to an already registered center --> 500 INTERNAL SERVER ERROR
    //  4) currentLoad doesn't exceed maxCapacity --> 500 INTERNAL SERVER ERROR
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


        // Handle coordinates
        boolean hasCoordinatesChanged = false;
        if (centerRequest.getCoordinates() != null) {
            Coordinates sourceCoords = centerRequest.getCoordinates();
            Coordinates targetCoords = updatedCenter.getCoordinates();
            if (sourceCoords.getLatitude() != null &&
                    !sourceCoords.getLatitude().equals(targetCoords.getLatitude())) {
                targetCoords.setLatitude(sourceCoords.getLatitude());
                hasCoordinatesChanged = true;
            }
            if (sourceCoords.getLongitude() != null &&
                    !sourceCoords.getLongitude().equals(targetCoords.getLongitude())) {
                targetCoords.setLongitude(sourceCoords.getLongitude());
                hasCoordinatesChanged = true;
            }
        }

        // Verify the new location doesn't already exist
        if(hasCoordinatesChanged) {
            Double latitude = updatedCenter.getCoordinates().getLatitude();
            Double longitude = updatedCenter.getCoordinates().getLongitude();
            boolean exists = centerRepository.existsByCoordinatesLatitudeAndCoordinatesLongitude(latitude, longitude);
            if (exists) {
                throw new RuntimeException("There is already a logistics center in that position.");
            }
        }
    }

    // Checks the given Center capacity is valid
    public boolean isValidCapacity(String capacity) {
        return capacity != null && VALID_CAPACITIES.contains(capacity);
    }

    // Checks the given center Center status is valid
    public boolean isValidStatus(String status) {
        return status != null && VALID_STATUS.contains(status);
    }
}
