package com.hackathon.inditex.Controllers;

import com.hackathon.inditex.DTO.CenterRequest;
import com.hackathon.inditex.Entities.Center;
import com.hackathon.inditex.Services.CenterManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/centers")
public class CenterManagementController {

    @Autowired
    private CenterManagementService centerManagementService;

    @PostMapping
    public ResponseEntity<?> createNewCenter(@RequestBody CenterRequest centerRequest) {
        String responseMessage = centerManagementService.createNewCenter(centerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    @GetMapping
    public ResponseEntity<?> readAllCenters() {
        List<Center> centerList = centerManagementService.readAllCenters();
        return ResponseEntity.ok(centerList);
    }

    @PatchMapping("{id}")
    public ResponseEntity<?> updateCenter(@PathVariable Long id, @RequestBody CenterRequest centerRequest) {
        String responseMessage = centerManagementService.updateCenter(id, centerRequest);
        return ResponseEntity.ok(responseMessage);
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> deleteCenter(@PathVariable Long id) {
        String responseMessage = centerManagementService.deleteCenter(id);
        return ResponseEntity.ok(responseMessage);
    }
}
