package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentCreationDto;
import ru.practicum.shareit.item.dto.ItemCreationDto;

import java.util.Map;

@Service
@Slf4j
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItems(Long ownerId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", ownerId, parameters);
    }

    public ResponseEntity<Object> getItem(int itemId, int userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> searchItems(long userId, String text) {

        return get("/search?text=" + text, userId);
    }

    public ResponseEntity<Object> postItem(ItemCreationDto itemCreationDto, long ownerId) {
        return post("", ownerId, itemCreationDto);
    }

    public ResponseEntity<Object> putItem(ItemCreationDto itemCreationDto, int itemId, long ownerId) {
        return patch("/" + itemId, ownerId, itemCreationDto);
    }

    public ResponseEntity<Object> postComment(CommentCreationDto commentCreationDto, int itemId, long authorId) {
        return post("/" + itemId + "/comment", authorId, commentCreationDto);
    }
}
