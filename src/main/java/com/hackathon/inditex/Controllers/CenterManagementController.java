package com.hackathon.inditex.Controllers;

import com.hackathon.inditex.DTO.CenterRequest;
import com.hackathon.inditex.Services.CenterManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
