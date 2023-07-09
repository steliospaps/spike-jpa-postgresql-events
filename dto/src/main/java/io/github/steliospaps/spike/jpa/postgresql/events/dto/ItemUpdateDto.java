package io.github.steliospaps.spike.jpa.postgresql.events.dto;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ItemUpdateDto {

    private ItemDto modified;

    private Integer deletedId;
}
