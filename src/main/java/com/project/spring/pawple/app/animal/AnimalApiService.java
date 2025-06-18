package com.project.spring.pawple.app.animal;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class AnimalApiService {

    // @Value("${ANIMAL_API_KEY:defaultValue}")
    // private String apiKey;

    private String apiKey="w4Z2XsyWOanjALTy0MMt%2FQiOl7cLztqw%2Fgi4ixy9fSp703rmUODqXnenU1i09aRrX3xKKy17S4jCsBGy1qYkJg%3D%3D" ;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getAnimalsJson() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate sevenDaysAgo = today.minusDays(6);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            String endDate = today.format(formatter);
            String beginDate = sevenDaysAgo.format(formatter);

            // 개
            URI dogUri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .path("/1543061/abandonmentPublicService_v2/abandonmentPublic_v2")
                    .queryParam("serviceKey", apiKey)
                    .queryParam("numOfRows", "50")
                    .queryParam("pageNo", "1")
                    .queryParam("_type", "json")
                    .queryParam("upkind", "417000")
                    .queryParam("bgnde", beginDate)
                    .queryParam("endde", endDate)
                    .build(true)
                    .toUri();
            String dogResult = restTemplate.getForObject(dogUri, String.class);

            // 고양이
            URI catUri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .path("/1543061/abandonmentPublicService_v2/abandonmentPublic_v2")
                    .queryParam("serviceKey", apiKey)
                    .queryParam("numOfRows", "50")
                    .queryParam("pageNo", "1")
                    .queryParam("_type", "json")
                    .queryParam("upkind", "422400")
                    .queryParam("bgnde", beginDate)
                    .queryParam("endde", endDate)
                    .build(true)
                    .toUri();
            String catResult = restTemplate.getForObject(catUri, String.class);

            JsonNode dogFiltered = filterByProcessState(dogResult, "보호중");
            JsonNode catFiltered = filterByProcessState(catResult, "보호중");

            ArrayNode resultArray = objectMapper.createArrayNode();
            addRandomAnimals(resultArray, dogFiltered, 4, 417000);  // 🐶 강아지
            addRandomAnimals(resultArray, catFiltered, 4, 422400);  // 🐱 고양이

            ObjectNode result = objectMapper.createObjectNode();
            result.set("animals", resultArray);

            return objectMapper.writeValueAsString(result);
        } catch (IOException e) {
            return "{\"error\": \"JSON 파싱 실패\"}";
        } catch (Exception e) {
            return "{\"error\": \"API 호출 실패\"}";
        }
    }

    private void addRandomAnimals(ArrayNode resultArray, JsonNode filtered, int maxCount, int upkindValue) {
        JsonNode items = filtered.path("response").path("body").path("items").path("item");
        if (items.isArray() && items.size() > 0) {
            int count = Math.min(maxCount, items.size());
            List<JsonNode> list = new ArrayList<>();
            items.forEach(list::add);
            Collections.shuffle(list);
            for (int i = 0; i < count; i++) {
                ObjectNode animal = (ObjectNode) list.get(i);
                animal.put("upkind", upkindValue);  // ✅ 강아지/고양이 타입 명시
                animal.put("desertionNo", list.get(i).path("desertionNo").asText());
                resultArray.add(animal);
            }
        }
    }

    // ✅ 필터링 메서드
    private JsonNode filterByProcessState(String json, String targetState) throws Exception {
        JsonNode root = objectMapper.readTree(json);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        ArrayNode filtered = objectMapper.createArrayNode();
        if (items.isArray()) {
            for (JsonNode item : items) {
                if ("보호중".equals(item.path("processState").asText())) {
                    filtered.add(item);
                }
            }
        }

        ObjectNode result = objectMapper.createObjectNode();
        result.set("response", objectMapper.createObjectNode()
                .set("body", objectMapper.createObjectNode()
                        .set("items", objectMapper.createObjectNode()
                                .set("item", filtered))));

        return result;
    }

    // private JsonNode pickRandomAnimal(JsonNode filtered) {
    //     JsonNode items = filtered.path("response").path("body").path("items").path("item");
    //     if (items.isArray() && items.size() > 0) {
    //         int randomIndex = new Random().nextInt(items.size());
    //         return items.get(randomIndex);
    //     }
    //     return objectMapper.nullNode();
    // }

    public String getAllRecentAnimalsJson() {
        try {
            LocalDate today = LocalDate.now();
            LocalDate sevenDaysAgo = today.minusDays(6);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

            String endDate = today.format(formatter);
            String beginDate = sevenDaysAgo.format(formatter);

            // 개
            URI dogUri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .path("/1543061/abandonmentPublicService_v2/abandonmentPublic_v2")
                    .queryParam("serviceKey", apiKey)
                    .queryParam("numOfRows", "50")
                    .queryParam("pageNo", "1")
                    .queryParam("_type", "json")
                    .queryParam("upkind", "417000")
                    .queryParam("bgnde", beginDate)
                    .queryParam("endde", endDate)
                    .build(true)
                    .toUri();
            String dogResult = restTemplate.getForObject(dogUri, String.class);

            // 고양이
            URI catUri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .path("/1543061/abandonmentPublicService_v2/abandonmentPublic_v2")
                    .queryParam("serviceKey", apiKey)
                    .queryParam("numOfRows", "50")
                    .queryParam("pageNo", "1")
                    .queryParam("_type", "json")
                    .queryParam("upkind", "422400")
                    .queryParam("bgnde", beginDate)
                    .queryParam("endde", endDate)
                    .build(true)
                    .toUri();
            String catResult = restTemplate.getForObject(catUri, String.class);

            JsonNode dogFiltered = filterByProcessState(dogResult, "보호중");
            JsonNode catFiltered = filterByProcessState(catResult, "보호중");

            ArrayNode resultArray = objectMapper.createArrayNode();
            addAllAnimals(resultArray, dogFiltered, 417000);
            addAllAnimals(resultArray, catFiltered, 422400);

            // 최신순 정렬
            List<JsonNode> sortedList = new ArrayList<>();
            resultArray.forEach(sortedList::add);
            sortedList.sort((a, b) -> b.path("noticeSdt").asText("").compareTo(a.path("noticeSdt").asText("")));

            ArrayNode sortedResult = objectMapper.createArrayNode();
            sortedList.forEach(sortedResult::add);

            ObjectNode result = objectMapper.createObjectNode();
            result.set("animals", sortedResult);
            return objectMapper.writeValueAsString(result);

        } catch (IOException e) {
            return "{\"error\": \"JSON 파싱 실패\"}";
        } catch (Exception e) {
            return "{\"error\": \"API 호출 실패\"}";
        }
    }

    private void addAllAnimals(ArrayNode resultArray, JsonNode filtered, int upkindValue) {
        JsonNode items = filtered.path("response").path("body").path("items").path("item");
        if (items.isArray() && items.size() > 0) {
            for (JsonNode item : items) {
                ObjectNode animal = (ObjectNode) item.deepCopy();

                animal.put("upkind", upkindValue);
                animal.put("desertionNo", item.path("desertionNo").asText());
                animal.put("sexCd", item.path("sexCd").asText());
                animal.put("happenPlace", item.path("happenPlace").asText());
                animal.put("specialMark", item.path("specialMark").asText());
                animal.put("noticeNo", item.path("noticeNo").asText());
                animal.put("kindNm", item.path("kindNm").asText());
                animal.put("age", item.path("age").asText());

                resultArray.add(animal);
            }
        }
    }

    public JsonNode getAnimalDetailByDesertionNo(String desertionNo) {
        try {
            URI uri = UriComponentsBuilder.newInstance()
                    .scheme("https")
                    .host("apis.data.go.kr")
                    .path("/1543061/abandonmentPublicService_v2/abandonmentPublic_v2")
                    .queryParam("serviceKey", apiKey)
                    .queryParam("_type", "json")
                    .queryParam("numOfRows", 1000) // 충분히 크게 설정
                    .build(true)
                    .toUri();

            String result = restTemplate.getForObject(uri, String.class);
            JsonNode root = objectMapper.readTree(result);
            JsonNode items = root.path("response").path("body").path("items").path("item");

            if (items.isArray()) {
                for (JsonNode item : items) {
                    if (item.has("desertionNo") &&
                        item.get("desertionNo").asText().equals(desertionNo)) {
                        return item;
                    }
                }
            } else if (items.has("desertionNo") &&
                    items.get("desertionNo").asText().equals(desertionNo)) {
                return items;
            }

            ObjectNode notFound = objectMapper.createObjectNode();
            notFound.put("error", "해당 desertionNo에 대한 데이터가 없습니다.");
            return notFound;

        } catch (Exception e) {
            ObjectNode error = objectMapper.createObjectNode();
            error.put("error", "API 호출 실패 또는 파싱 오류");
            return error;
        }
    }



}
