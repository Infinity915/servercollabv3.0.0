package com.studencollabfin.server.controller;

import com.studencollabfin.server.model.CollabPod;
import com.studencollabfin.server.repository.CollabPodRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/pods")
public class CollabPodController {

    private final CollabPodRepository collabPodRepository;

    public CollabPodController(CollabPodRepository collabPodRepository) {
        this.collabPodRepository = collabPodRepository;
    }

    @GetMapping
    public ResponseEntity<List<CollabPod>> getAllPods() {
        List<CollabPod> pods = collabPodRepository.findAll();
        return ResponseEntity.ok(pods);
    }

    /**
     * This is the endpoint for creating a new collaboration pod.
     * It receives pod data from the React form and saves it to the database.
     */
    @PostMapping
    public ResponseEntity<CollabPod> createPod(@RequestBody CollabPod newPod) {
        CollabPod savedPod = collabPodRepository.save(newPod);
        return new ResponseEntity<>(savedPod, HttpStatus.CREATED);
    }
}
