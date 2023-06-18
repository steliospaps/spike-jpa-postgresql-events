package io.github.steliospaps.spike.jpa.postgresql.events.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.github.steliospaps.spike.jpa.postgresql.events.dto.ItemDto;
import io.github.steliospaps.spike.jpa.postgresql.events.service.Service;


@RestController
public class Controller {

    @Autowired
    private Service service;
    
    @GetMapping(path = "/api/item",produces = {MediaType.APPLICATION_JSON_VALUE})
    List<ItemDto> getItems(){
        return service.getItems();
    }

    @GetMapping(path = "/api/item/{id}",produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<ItemDto> getItem(@PathVariable int id){
        return ResponseEntity.of(service.getItem(id));
    }

    @DeleteMapping(path = "/api/item/{id}")
    void deleteItem(@PathVariable int id){
        service.deleteItem(id);
    }

    @PostMapping(path = "/api/item",consumes= {MediaType.APPLICATION_JSON_VALUE}, produces = {MediaType.APPLICATION_JSON_VALUE})
    ResponseEntity<ItemDto> postItem(@RequestBody ItemDto item){
        return ResponseEntity.of(service.upsert(item));
    }
    
}
