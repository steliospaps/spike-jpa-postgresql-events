package io.github.steliospaps.spike.jpa.postgresql.events.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ItemDto {
    private Integer id;
    private String name;
    private Long value;
}
