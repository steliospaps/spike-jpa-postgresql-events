package io.github.steliospaps.spike.jpa.postgresql.events.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.steliospaps.spike.jpa.postgresql.events.dto.ItemDto;
import io.github.steliospaps.spike.jpa.postgresql.events.dto.ItemUpdateDto;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.client.ReactorNettyWebSocketClient;
import reactor.core.Disposable;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class Client implements AutoCloseable {

    private WebClient webClient;
    private ReactorNettyWebSocketClient webSocketClient;
    private String wsUrlBase;
    private ObjectMapper objecMapper;
    private URI uriEventItem;
    private List<Disposable> disposables = new CopyOnWriteArrayList<>();

    public Client(String baseUrl) {
        webClient = WebClient.builder().baseUrl(baseUrl)//
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)//
                .build();

        wsUrlBase = baseUrl.replaceFirst("^http://", "ws://").replaceFirst("^https://", "wss://");

        try {
            uriEventItem = new URI(wsUrlBase + "/events/item");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        webSocketClient = new ReactorNettyWebSocketClient();

        objecMapper = new ObjectMapper();
    }

    @Override
    public void close() throws Exception {
        List<Disposable> toclear = List.copyOf(disposables);
        disposables.clear();
        toclear.forEach(Disposable::dispose);
    }

    // TODO: change this signature to expose flux
    public void subscribeItem(Consumer<ItemDto> modifiedCallBack, Consumer<Integer> deletedIdCallback) {

        // TODO: make it shareable (one websocket per cosumer set)
        // TODO: make it to reconnect on error or termination
        // TODO: allow cancelation
        disposables.add(webSocketClient.execute(uriEventItem, ws -> {
            String loggerName = "ws.client." + ws.getId();

            LoggerFactory.getLogger(loggerName).info("starting {}", ws);

            return ws.send(ws.receive().map(WebSocketMessage::getPayloadAsText)//
                    .map(i -> toItemUpdate(i))//
                    .log(loggerName)//
                    .doOnNext(update -> {
                        Optional.ofNullable(update.getModified()).ifPresent(modifiedCallBack::accept);
                        Optional.ofNullable(update.getDeletedId()).ifPresent(deletedIdCallback::accept);
                    }).ignoreElements().map(i -> ws.textMessage("")));
        }).subscribe());
    }

    private ItemUpdateDto toItemUpdate(String i) {
        try {
            return objecMapper.readValue(i, ItemUpdateDto.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public Mono<ItemDto> upsert(ItemDto item) {
        return this.webClient.post().uri(ub -> ub.path("/api/item").build())//
                .bodyValue(item)//
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(ItemDto.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }

    public Mono<ItemDto> getItem(Integer id) {
        return this.webClient.get().uri(ub -> ub.path("/api/item/{id}").build(id))//
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(ItemDto.class);
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }

    public Mono<List<ItemDto>> getItems() {
        return this.webClient.get().uri(ub -> ub.path("/api/item").build())//
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(new ParameterizedTypeReference<List<ItemDto>>() {
                        });
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }

    public Mono<Void> deleteItem(Integer id) {
        return this.webClient.delete().uri(ub -> ub.path("/api/item/{id}").build(id))//
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return Mono.empty();
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                });
    }

}
