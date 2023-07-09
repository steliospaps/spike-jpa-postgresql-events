package io.github.steliospaps.spike.jpa.postgresql.events.db.listen;

public record ChangedById(String table, String type, String id) {

}
