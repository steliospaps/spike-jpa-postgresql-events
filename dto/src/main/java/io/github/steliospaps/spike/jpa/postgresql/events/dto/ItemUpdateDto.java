package io.github.steliospaps.spike.jpa.postgresql.events.dto;

import java.util.Optional;

import lombok.Data;

@Data
public class ItemUpdateDto {
    Optional<ItemDto> modified;
    Optional<Integer> deletedId;
}
