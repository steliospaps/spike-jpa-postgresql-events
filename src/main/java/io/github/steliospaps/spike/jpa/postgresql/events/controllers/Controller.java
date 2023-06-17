package io.github.steliospaps.spike.jpa.postgresql.events.controllers;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.steliospaps.spike.jpa.postgresql.events.dto.ItemDto;

@RestController
public class Controller {

    @GetMapping(path = "/api/item",produces = {MediaType.APPLICATION_JSON_VALUE})
    List<ItemDto> getItems(){
        return List.of(ItemDto.builder().id(1).name("hello").value(1234L).build());
    }
    
}
