package com.miles.beauminity.controller.store;

import java.net.URI;

import java.nio.charset.StandardCharsets;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;


@RestController
@RequestMapping("/api/store")
public class StoreApiController {
    private final String CLIENT_ID ="qBf8p8fqAgG9S9VMSebp";
    private final String CLIENT_SECRET ="l3FLwmhvfL";

    @GetMapping("/store")
    public ResponseEntity<String> getMethodName(@RequestParam("keyword") String keyword) {

        // uri 생성
        URI uri =UriComponentsBuilder
            .fromUriString("https://openapi.naver.com")
            .path("/v1/search/shop.json")
            .queryParam("query",keyword) // 카테고리 역할
            .queryParam("display",10)
            .encode(StandardCharsets.UTF_8)
            .build()
            .toUri();

        //헤더 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id",CLIENT_ID);
        headers.set("X-Naver-Client-Secret",CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_JSON); //타입 지정
        //요청
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET, // 가져오라고 요청
                requestEntity,
                String.class
            );
            return ResponseEntity.ok(response.getBody());
        }catch(org.springframework.web.client.HttpStatusCodeException e) {
           return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("API 요청 둥 오류가 발생했습니다.");
        }
    }

}
