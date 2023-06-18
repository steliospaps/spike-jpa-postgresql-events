package io.github.steliospaps.spike.jpa.postgresql.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.Assert.assertThrows;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

import io.github.steliospaps.spike.jpa.postgresql.events.client.Client;
import io.github.steliospaps.spike.jpa.postgresql.events.db.ItemRepository;
import io.github.steliospaps.spike.jpa.postgresql.events.dto.ItemDto;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SpikeJpaPostgresqlEventsApplicationTest {

    private static final Duration TIMEOUT = Duration.ofSeconds(5);

    @Value(value = "${local.server.port}")
    private int port;

    private Client client;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void setup() {
        itemRepository.deleteAll();

        client = new Client("http://localhost:" + port);
    }

    @AfterEach
    void teardown() throws Exception {
        client.close();
    }

    @Test
    void testCreateReadUpdateDelete() {
        List<ItemDto> modified = new CopyOnWriteArrayList<>();
        List<Integer> deleted = new CopyOnWriteArrayList<>();

        client.subscribeItem(item -> modified.add(item), deletedId -> deleted.add(deletedId));

        // when creating
        ItemDto created = client.upsert(ItemDto.builder().name("stelios").value(4L).build()).block(TIMEOUT);

        // then
        assertThat(created.getName()).isEqualTo("stelios");
        assertThat(created.getValue()).isEqualTo(4L);

        await().untilAsserted(() -> assertThat(modified).anyMatch(i -> i.getId() == created.getId()));

        assertThat(client.getItem(created.getId()).block(TIMEOUT)).isEqualTo(created);

        assertThat(client.getItems().block(TIMEOUT)).isEqualTo(List.of(created));

        assertThat(deleted).isEmpty();

        // given
        modified.clear();

        // when updating
        ItemDto updated = client.upsert(created.toBuilder().name("stelios1").build()).block(TIMEOUT);

        // then
        assertThat(updated.getName()).isEqualTo("stelios1");
        assertThat(updated.getValue()).isEqualTo(4L);

        await().untilAsserted(() -> assertThat(modified).anyMatch(i -> i.equals(updated)));

        assertThat(client.getItem(created.getId()).block(TIMEOUT)).isEqualTo(created);

        assertThat(client.getItems().block(TIMEOUT)).isEqualTo(List.of(created));

        assertThat(deleted).isEmpty();

        // given
        modified.clear();

        // when deleting
        client.deleteItem(updated.getId()).block(TIMEOUT);

        await().untilAsserted(() -> assertThat(modified).anyMatch(i -> i.equals(updated)));

        assertThrows(Exception.class, () -> client.getItem(created.getId()).block(TIMEOUT));

        assertThat(client.getItems().block(TIMEOUT)).isEqualTo(List.of());

        assertThat(deleted).containsExactly(updated.getId());

    }

}
