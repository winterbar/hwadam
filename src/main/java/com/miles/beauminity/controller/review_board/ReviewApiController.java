package com.miles.beauminity.controller.review_board;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.net.URI;
import java.nio.charset.StandardCharsets;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
public class ReviewApiController {
    
    private final String CLIENT_ID ="rfulAKUI_G_GhOE7CIgi";
    private final String CLIENT_SECRET ="XPl1EGqljQ";

    @GetMapping("/search")
    public ResponseEntity<String> searchProduct(@RequestParam("keyword") String keyword) {
        
        // 1. 네이버 쇼핑 검색 api 주소 설정 (한글 깨짐 방지 위한 UTF-8 인코딩 포함)
        URI uri =UriComponentsBuilder
            .fromUriString("https://openapi.naver.com")
            .path("/v1/search/shop.json")
            .queryParam("query",keyword)
            .queryParam("display",10) // 화면에 노출할 결과 개수 (최대 10개)
            .encode(StandardCharsets.UTF_8)
            .build()
            .toUri();

        // 2. 네이버 API 인증을 위한 헤더(Header) 데이터 설정
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Naver-Client-Id", CLIENT_ID );
        headers.set("X-Naver-Client-Secret", CLIENT_SECRET);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        RestTemplate restTemplate = new RestTemplate();

        try {
            // 3. 네이버 서버로 HTTP GET 요청을 보내고 결과(JSON 문자열)를 받아옴
            ResponseEntity<String> response = restTemplate.exchange(
                uri,
                HttpMethod.GET,
                requestEntity,
                String.class
            );
            // 4. 네이버가 돌려준 JSON 데이터를 우리 화면(브라우저)으로 그대로 전달
            return ResponseEntity.ok(response.getBody());

        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            // 네이버 서버가 에러 코드를 뱉은 경우 (401, 403 등) 어떤 응답을 줬는지 로그 확인
            System.out.println("네이버 API 반환 에러 코드"+ e.getStatusCode());
            System.out.println("네이버 API 반환 본문: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("API 요청 둥 오류가 발생했습니다.");
        }
    }
    
}
