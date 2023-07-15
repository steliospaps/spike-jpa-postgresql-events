# about

![master](https://github.com/steliospaps/spike-jpa-postgresql-events/actions/workflows/java_ci.yml/badge.svg?branch=master&event=push)

# interaction

```mermaid
sequenceDiagram
    participant client1
    participant client2
    participant app1 as App Instance 1
    participant app2 as App Instance 2
    app1 ->> DB: listen
    client1 -->> app1: init websocket
    client2 ->>+ app2: REST operation
    app2 ->> DB: update id1
    app2 ->>- client2: response
    DB ->> app1: update event id1
    app1 ->>+ DB: select id1
    DB ->>- app1: changed entity
    app1 -->> client1: stream update

```

# dev

## swagger

[swagger](http://localhost:8080/swagger-ui.html)

## local dev db (docker-compose)

### start local dev db

```
docker-compose up -d
```

### interact with local dev db

```
docker exec -it $(docker-compose ps -q) psql -d dev_db -U dev
```

### run locally

```
./gradlew bootRun
```

Connect to websocket `ws://localhost:8080/events/item`

Interact via swagger (see above)