package com.example.spring.redis.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.spring.redis.model.Tutorial;
import com.example.spring.redis.service.TutorialService;

import javax.annotation.PostConstruct;

@CrossOrigin(origins = "http://localhost:8081")
@RestController
@RequestMapping("/api")
public class TutorialController {

  private List<Long> tutorialIds = new ArrayList<>();
  private List<Tutorial> tutorials = new ArrayList<>(); // Define tutorials as a class variable

  @Autowired
  TutorialService tutorialService;

  @PostConstruct
  public void init() {
      try {
          tutorials = tutorialService.findAll(); // Use the existing findAll() method
          for (Tutorial tutorial : tutorials) {
              tutorialIds.add(tutorial.getId());
          }
      } catch (Exception e) {
          // Handle exception
      }
  }

 @GetMapping("/tutorials")
  public ResponseEntity<List<Tutorial>> getAllTutorials(@RequestParam(required = false) String title) {
    try {
      List<Tutorial> tutorials = new ArrayList<Tutorial>();

      // If no title is provided, fetch all tutorials
      if (title == null)
        tutorialService.findAll().forEach(tutorials::add);
      // If a title is provided, fetch tutorials containing the title
      else
        tutorialService.findByTitleContaining(title).forEach(tutorials::add);

      // If no tutorials are found, return a NO_CONTENT status
      if (tutorials.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }

      // If tutorials are found, return the list of tutorials with an OK status
      return new ResponseEntity<>(tutorials, HttpStatus.OK);
    } catch (Exception e) {
      // In case of any exception, return an INTERNAL_SERVER_ERROR status
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @GetMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> getTutorialById(@PathVariable("id") long id) {
    Optional<Tutorial> tutorialData = tutorialService.findById(id);

    if (tutorialData.isPresent()) {
      return new ResponseEntity<>(tutorialData.get(), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }



  @PostMapping("/tutorials/init-data")
  public ResponseEntity<List<Tutorial>> createTutorials() {
      try {
          List<Tutorial> tutorials = new ArrayList<>();
          for (int i = 0; i < 30; i++) {
              String title = "Tutorial " + (i + 1);
              String description = "Description for tutorial " + (i + 1);
              Tutorial _tutorial = tutorialService.save(new Tutorial(title, description, false));
              tutorials.add(_tutorial);
              tutorialIds.add(_tutorial.getId());
          }
          return new ResponseEntity<>(tutorials, HttpStatus.CREATED);
      } catch (Exception e) {
          return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
      }
  }

  @PutMapping("/tutorials/{id}")
  public ResponseEntity<Tutorial> updateTutorial(@PathVariable("id") long id, @RequestBody Tutorial tutorial) {
    Optional<Tutorial> tutorialData = tutorialService.findById(id);

    if (tutorialData.isPresent()) {
      Tutorial _tutorial = tutorialData.get();
      _tutorial.setTitle(tutorial.getTitle());
      _tutorial.setDescription(tutorial.getDescription());
      _tutorial.setPublished(tutorial.isPublished());
      return new ResponseEntity<>(tutorialService.update(_tutorial), HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
  }

  @PostMapping("/tutorials")
  public ResponseEntity<Tutorial> createTutorial(@RequestBody Tutorial tutorial) {
    try {
      Tutorial _tutorial = tutorialService.save(new Tutorial(tutorial.getTitle(), tutorial.getDescription(), false));
      tutorials.add(_tutorial);
      tutorialIds.add(_tutorial.getId());
      return new ResponseEntity<>(_tutorial, HttpStatus.CREATED);
    } catch (Exception e) {
      return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  // Rest of your methods...

  @DeleteMapping("/tutorials/{id}")
  public ResponseEntity<HttpStatus> deleteTutorial(@PathVariable("id") long id) {
    try {
      tutorialService.deleteById(id);
      tutorialIds.remove(id);
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  @DeleteMapping("/tutorials")
  public ResponseEntity<HttpStatus> deleteAllTutorials() {
    try {
      tutorialService.deleteAll();
      tutorialIds.clear();
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

  }

  @GetMapping("/tutorials/published")
  public ResponseEntity<List<Tutorial>> findByPublished() {
    try {
      List<Tutorial> tutorials = tutorialService.findByPublished(true);

      if (tutorials.isEmpty()) {
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
      }
      return new ResponseEntity<>(tutorials, HttpStatus.OK);
    } catch (Exception e) {
      return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }
}



