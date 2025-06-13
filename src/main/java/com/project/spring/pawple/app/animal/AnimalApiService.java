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

    private String apiKey="u2K8r3OXbZkp2ka33s370Hes67Dd2PyVMhq/lfz6KrOB5enFKlSdoPSg5Zy8xRMIuhi3o//zU5lc80pxEvvmtw==" ;

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
                    .build(false)
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
                    .build(false)
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

    private JsonNode pickRandomAnimal(JsonNode filtered) {
        JsonNode items = filtered.path("response").path("body").path("items").path("item");
        if (items.isArray() && items.size() > 0) {
            int randomIndex = new Random().nextInt(items.size());
            return items.get(randomIndex);
        }
        return objectMapper.nullNode();
    }
}
