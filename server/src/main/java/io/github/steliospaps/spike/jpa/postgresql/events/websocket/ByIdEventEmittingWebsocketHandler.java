package io.github.steliospaps.spike.jpa.postgresql.events.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.steliospaps.spike.jpa.postgresql.events.db.listen.ChangedById;
import io.github.steliospaps.spike.jpa.postgresql.events.dto.ItemUpdateDto;
import io.github.steliospaps.spike.jpa.postgresql.events.service.ItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

@Slf4j
public class ByIdEventEmittingWebsocketHandler implements WebSocketHandler {

    Sinks.Many<String> sink = Sinks.many().multicast().directBestEffort();

    @Value("${websocket.sender.buffer:100}")
    private int bufferSize;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ItemService service;

    @Override
    public Mono<Void> handle(WebSocketSession session) {

        Mono<?> read = session.receive().then();

        Mono<Void> write = session.send(sink.asFlux().onBackpressureBuffer(bufferSize, BufferOverflowStrategy.ERROR).map(session::textMessage));

        return Mono.zip(read, write.then()).then();
    }

    @EventListener
    public void onEvent(ChangedById changedById) {
        try {
            if (!changedById.table().equals("item")) {
                return; //skip
            }
            ItemUpdateDto.ItemUpdateDtoBuilder msg = ItemUpdateDto.builder();

            switch (changedById.type()) {
                case "INSERT":
                case "UPDATE":
                    msg.modified(service.getItem(Integer.parseInt(changedById.id())).get());
                    break;
                case "DELETE":
                    msg.deletedId(Integer.parseInt(changedById.id()));
                    break;
                default:
                    return; //skip
            }

            String str = mapper.writeValueAsString(msg.build());

            Sinks.EmitResult res = sink.tryEmitNext(str);

            if (res.isFailure()) {
                res.describeConstable().ifPresent(i -> log.error("failed to emit {} reason {}", changedById, i));
            }
        } catch (JsonProcessingException e) {

            log.error("failed to process {} will drop", changedById);
        }
    }
}
