package io.github.steliospaps.spike.jpa.postgresql.events;

import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import io.github.steliospaps.spike.jpa.postgresql.events.db.entity.Item;
import io.github.steliospaps.spike.jpa.postgresql.events.dto.ItemDto;

@org.mapstruct.Mapper
public interface DtoEntityMapper {

    public static final DtoEntityMapper INSTANCE = Mappers.getMapper(DtoEntityMapper.class);

    public ItemDto entityToDto(Item item);

    public Item dtoToEntity(ItemDto item);
   
    public Item updateEntityFromDto(@MappingTarget Item i, ItemDto item);

}
