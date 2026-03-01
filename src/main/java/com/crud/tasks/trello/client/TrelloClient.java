package com.crud.tasks.trello.client;

import com.crud.tasks.config.TrelloConfig;
import com.crud.tasks.domain.CreatedTrelloCard;
import com.crud.tasks.domain.TrelloBoardDto;
import com.crud.tasks.domain.TrelloCardDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TrelloClient {

    private final RestTemplate restTemplate;
    private final TrelloConfig trelloConfig;
    private static final Logger LOGGER = LoggerFactory.getLogger(TrelloClient.class);

    private static final String MEMBERS = "/members/";
    private static final String BOARDS = "/boards";
    private static final String CARDS = "/cards";

    private static final String PARAM_KEY = "key";
    private static final String PARAM_TOKEN = "token";
    private static final String PARAM_FIELDS = "fields";
    private static final String PARAM_LISTS = "lists";

    private static final String FIELDS_VALUE = "name,id";
    private static final String LISTS_VALUE = "all";

    public List<TrelloBoardDto> getTrelloBoards() {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + MEMBERS + trelloConfig.getUsername() + BOARDS)
                .queryParam(PARAM_KEY, trelloConfig.getTrelloAppKey())
                .queryParam(PARAM_TOKEN, trelloConfig.getTrelloToken())
                .queryParam(PARAM_FIELDS, FIELDS_VALUE)
                .queryParam(PARAM_LISTS, LISTS_VALUE)
                .build()
                .encode()
                .toUri();

        try {
            TrelloBoardDto[] boardsResponse = restTemplate.getForObject(url, TrelloBoardDto[].class);
            return Optional.ofNullable(boardsResponse)
                    .map(Arrays::asList)
                    .orElse(Collections.emptyList())
                    .stream()
                    .filter(p -> Objects.nonNull(p.getId()) && Objects.nonNull(p.getName()))
                    .collect(Collectors.toList());
        } catch (RestClientException e) {
            LOGGER.error(e.getMessage(), e);
            return Collections.emptyList();
        }
    }

    public CreatedTrelloCard createNewCard(TrelloCardDto trelloCardDto) {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + CARDS)
                .queryParam(PARAM_KEY, trelloConfig.getTrelloAppKey())
                .queryParam(PARAM_TOKEN, trelloConfig.getTrelloToken())
                .queryParam("name", trelloCardDto.getName())
                .queryParam("desc", trelloCardDto.getDescription())
                .queryParam("pos", trelloCardDto.getPos())
                .queryParam("idList", trelloCardDto.getIdList())
                .build()
                .encode()
                .toUri();

        return restTemplate.postForObject(url, null, CreatedTrelloCard.class);
    }

    private URI getUri() {
        URI url = UriComponentsBuilder.fromHttpUrl(trelloConfig.getTrelloApiEndpoint() + MEMBERS + trelloConfig.getUsername() + BOARDS)
                .queryParam(PARAM_KEY, trelloConfig.getTrelloAppKey())
                .queryParam(PARAM_TOKEN, trelloConfig.getTrelloToken())
                .queryParam(PARAM_FIELDS, "name,id")
                .queryParam(PARAM_LISTS, "all")
                .build()
                .encode()
                .toUri();
        return url;
    }

}

