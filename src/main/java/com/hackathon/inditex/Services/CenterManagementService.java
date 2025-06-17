package com.hackathon.inditex.Services;

import com.hackathon.inditex.DTO.CenterRequest;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Repositories.CenterRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;

@Service
public class CenterManagementService {
    @Autowired
    private CenterRepository centerRepository;

    public String createNewCenter(CenterRequest centerRequest){
        Double lat = centerRequest.getCoordinates().getLatitude();
        Double lon = centerRequest.getCoordinates().getLongitude();
        int currentLoad = centerRequest.getCurrentLoad();
        int maxCapacity = centerRequest.getMaxCapacity();

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
}
