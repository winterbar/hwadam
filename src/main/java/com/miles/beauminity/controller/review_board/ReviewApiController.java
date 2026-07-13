package com.miles.beauminity.controller.review_board;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.miles.beauminity.service.review_board.ReviewService;
import com.miles.beauminity.vo.board.MasterBoardLikeVO;

import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewApiController { // 역할: 네이버 api 사용요청 처리

    private final ReviewService reviewService; 
    
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


      // =======================================================
    // 2. 좋아요 상태 불러오기 (최종 주소: /api/board/{boardId}/like-check)
    // =======================================================
    @GetMapping("/review/{boardId}/like-check") 
    public ResponseEntity<Map<String, Object>> likeStatus(
        @PathVariable("boardId") Long boardId,
        @AuthenticationPrincipal UserDetails userDetails) { // 💡 시큐리티가 로그인 유저를 알아서 주입해 줍니다.

    Map<String, Object> result = new HashMap<>();

    // 비로그인 상태 체크
    if (userDetails == null) {
        result.put("isLikeOn", false);
        return ResponseEntity.ok(result);
    }

    String username = userDetails.getUsername(); // 💡 안전하게 로그인 ID 추출
    boolean isLikeON = reviewService.isLikeON(boardId, username);

    result.put("isLikeOn", isLikeON);
    return ResponseEntity.ok(result);
    }

    // =======================================================
    // 3. 좋아요 누르기 / 취소하기 토글 (최종 주소: /api/board/{boardId}/toggle-like)
    // =======================================================
    @PostMapping("/review/{boardId}/toggle-like")
    public ResponseEntity<Map<String, Object>> toggleLike(
        @PathVariable("boardId") Long boardId,
        @AuthenticationPrincipal UserDetails userDetails) {

    Map<String, Object> result = new HashMap<>();

    // 비로그인 상태일 때 401 에러
    if (userDetails == null) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    String username = userDetails.getUsername();

    MasterBoardLikeVO masterBoardLikeVO = new MasterBoardLikeVO();
    masterBoardLikeVO.setBoardId(boardId);
    masterBoardLikeVO.setUsername(username);

    boolean isLikeONBefore = reviewService.isLikeON(boardId, username);
    System.out.println("현재 상태" + isLikeONBefore);

    if (!isLikeONBefore) {
        reviewService.insertLike(masterBoardLikeVO);
    } else {
        reviewService.deleteLike(masterBoardLikeVO);
    }

    int likeCnt = reviewService.getLikeCount(boardId);

    result.put("isLikeOn", !isLikeONBefore);
    result.put("likeCount", likeCnt);

    return ResponseEntity.ok(result);
    }


}
