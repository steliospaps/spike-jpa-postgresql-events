package io.github.steliospaps.spike.jpa.postgresql.events.db;

import org.springframework.data.jpa.repository.JpaRepository;

import io.github.steliospaps.spike.jpa.postgresql.events.db.entity.Item;

public interface ItemRepository extends JpaRepository<Item, Integer> {

}
