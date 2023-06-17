package io.github.steliospaps.spike.jpa.postgresql.events.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ProblemDetail;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import io.github.steliospaps.spike.jpa.postgresql.events.DtoEntityMapper;
import io.github.steliospaps.spike.jpa.postgresql.events.db.ItemRepository;
import io.github.steliospaps.spike.jpa.postgresql.events.dto.ItemDto;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Transactional(readOnly = true)
public class Service {

    @Autowired
    private ItemRepository itemRepository;

    private DtoEntityMapper mapper = DtoEntityMapper.INSTANCE;

    
    public List<ItemDto> getItems() {
        return itemRepository.findAll().stream().map(i -> mapper.entityToDto(i)).toList();
    }

    public Optional<ItemDto> getItem(int id) {
        return itemRepository.findById(id).map(i -> mapper.entityToDto(i));
    }

    @Transactional
    public Optional<ItemDto> upsert(ItemDto item) {
        if (item.getId() == null) {
            return Optional.of(item)//
                    .map(i -> {
                        log.debug("inserting {}", i);
                        return i;
                    }).map(mapper::dtoToEntity)//
                    .map(itemRepository::save)//
                    .map(mapper::entityToDto);
        } else {
            return itemRepository.findById(item.getId())//
                    .map(i -> mapper.updateEntityFromDto(i, item))//
                    .map(itemRepository::save)//
                    .map(mapper::entityToDto);
        }
    }

    @Transactional
    public void deleteItem(int id) {
        itemRepository.deleteById(id);
    }

}
