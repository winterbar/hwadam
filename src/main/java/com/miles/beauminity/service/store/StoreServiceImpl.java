package com.miles.beauminity.service.store;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.miles.beauminity.mapper.store.StoreMapper;
import com.miles.beauminity.vo.store.StoreResponseVO;
import com.miles.beauminity.vo.store.StoreVO;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoreServiceImpl implements StoreService {


    private final StoreMapper storeMapper;
    private static final String CLIENT_ID = "qBf8p8fqAgG9S9VMSebp";
    private static final String CLIENT_SECRET = "l3FLwmhvfL";

    
    //화면에 출력할 전체 상품 목록을 조회
    //DB에 상품이 없으면 네이버 쇼핑 API에서 상품을 가져와 저장
   
    @Override
    public List<StoreVO> getStoreList() {

        // DB에 저장된 전체 상품 조회
        List<StoreVO> storeList = storeMapper.selectStoreList();

        // DB가 비어 있을 때만 네이버 API를 호출
        if (storeList == null || storeList.isEmpty()) {
            getNaverProducts("화장품");
            // API 상품 저장 후 다시 DB에서 상품을 조회
            storeList = storeMapper.selectStoreList();
        }

        // 조회 결과가 null이면 빈 목록을 반환
        if (storeList == null) {
            return Collections.emptyList();
        }
        return storeList;
    }

    //전달받은 검색어로 네이버 쇼핑 API를 호출
    //검색 결과를 store 테이블에 저장

    @Override
    public void getNaverProducts(String keyword) {

        // 네이버 쇼핑 검색 API 요청 주소 생성
        URI uri = UriComponentsBuilder
                .fromUriString("https://openapi.naver.com")
                .path("/v1/search/shop.json")
                .queryParam("query", keyword)
                .queryParam("display", 100)
                .queryParam("start", 1)
                .queryParam("sort", "sim")
                .encode(StandardCharsets.UTF_8)
                .build()
                .toUri();

        // 네이버 API 인증 정보를 요청 헤더에 추가
        HttpHeaders headers = new HttpHeaders();

        headers.set(
                "X-Naver-Client-Id",
                CLIENT_ID
        );
        headers.set(
                "X-Naver-Client-Secret",
                CLIENT_SECRET
        );

        // 헤더 정보를 담은 요청 객체 생성
        HttpEntity<Void> requestEntity =new HttpEntity<>(null, headers);

        RestTemplate restTemplate =new RestTemplate();

        // 네이버 쇼핑 API에 GET 요청을 보내고 응답 받음
        ResponseEntity<StoreResponseVO> response =
                restTemplate.exchange(
                        uri,
                        HttpMethod.GET,
                        requestEntity,
                        StoreResponseVO.class
                );

        StoreResponseVO responseVO =response.getBody();

        // 응답 데이터나 상품 목록이 없으면 저장하지 않고 종료
        if (responseVO == null ||responseVO.getItems() == null) {
            return;
        }

        // API에서 받은 상품을 하나씩 DB에 저장
        for (StoreVO product : responseVO.getItems()) {

            // 상품 번호가 없는 상품은 저장X
            if (product.getProductId() == null) {
                continue;
            }
            // 상품명에 포함된 HTML 태그를 제거
            product.setTitle(removeHtmlTag(product.getTitle()));
            // DB의 NOT NULL 조건에 맞게 null 값을 빈 문자열로 변경
            product.setImage(nullToEmpty(product.getImage()));
            product.setBrand(nullToEmpty(product.getBrand()));
            product.setCategory1(nullToEmpty(product.getCategory1()));
            product.setCategory2(nullToEmpty(product.getCategory2()));

            // 정리한 상품 정보를 store 테이블에 저장
            storeMapper.insertStore(product);
        }
    }


     // 상품명에 포함된 HTML 태그 제거

    private String removeHtmlTag(String title) {

        if (title == null) {
            return "";
        }

        return title.replaceAll("<[^>]*>", "");
    }

    //문자열이 null이면 빈 문자열을 반환
     
    private String nullToEmpty(String value) {

        if (value == null) {
            return "";
        }

        return value;
    }
}