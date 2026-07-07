package com.miles.beauminity.controller.qna_board;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miles.beauminity.service.qna_board.QnaService;
import com.miles.beauminity.vo.board.MasterBoardFileVO;
import com.miles.beauminity.vo.board.MasterBoardLikeVO;
import com.miles.beauminity.vo.board.MasterBoardReplyVO;
import com.miles.beauminity.vo.board.PageVO;
import com.miles.beauminity.vo.login.MemberVO;
import com.miles.beauminity.vo.qna_board.CommunityReplyVO;
import com.miles.beauminity.vo.qna_board.QnaBoardCompleteVO;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@AllArgsConstructor
@RequestMapping("/api/board")
public class QnaApiController {
    private QnaService qnaService;

    public String getUsername() {

        // 멤버 정보 가져오기
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        System.out.println("현재 로그인한 회원: " + username);

        return username;
    }

    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterBoard(
            @RequestParam(value = "category", defaultValue = "전체보기") String category, @ModelAttribute PageVO pageVO,
            @RequestParam(value = "sort", defaultValue = "최신순") String sort,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(value = "searchType", defaultValue = "titleContent") String searchType,
            @RequestParam(required = false) String keyword) {
        Map<String, Object> result = new HashMap<>();
        // 카테고리에 맞는 리스트를 DB에서 조회해서 반환

        System.out.println("검색 종류: " + searchType);

        String type = "qna";

        int count = qnaService.getQnaCountByCategory(type, category);
        pageVO.pageInfo(count);

        System.out.println("페이지 정보:" + pageVO.toString());

        List<QnaBoardCompleteVO> filteredList = qnaService.getQnaBoardByCategory(type, pageVO, category, sort,
                startDate, endDate, searchType, keyword);

        result.put("list", filteredList);
        result.put("pageInfo", pageVO);

        return ResponseEntity.ok(result);
    }

    // 좋아요 불러오기
    @GetMapping("/{boardId}/like-check")
    public ResponseEntity<Map<String, Object>> likeStatus(
            @PathVariable("boardId") Long id) {

        Map<String, Object> result = new HashMap<>();

        String username = getUsername();

        if (username == null) {
            result.put("isLikeOn", false);
            return ResponseEntity.ok(result);
        }

        boolean isLikeON = qnaService.isLikeON(id, username);

        result.put("isLikeOn", isLikeON);

        return ResponseEntity.ok(result);
    }

    // 좋아요 누르면
    @PostMapping("/{boardId}/toggle-like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            @PathVariable("boardId") Long id) {

        Map<String, Object> result = new HashMap<>();

        String username = getUsername();

        if (username == null) {
            return ResponseEntity.status(401).build();
        }

        MasterBoardLikeVO masterBoardLikeVO = new MasterBoardLikeVO();
        masterBoardLikeVO.setBoardId(id);
        masterBoardLikeVO.setUsername(username);

        boolean isLikeONBefore = qnaService.isLikeON(id, username);

        // 클릭했을 때 좋아요 없으면 좋아요 추가
        if (!isLikeONBefore) {

            qnaService.insertLike(masterBoardLikeVO);
        }
        // 클릭했을 때 좋아요 있으면 좋아요 취소
        else {
            qnaService.deleteLike(masterBoardLikeVO);
        }

        // 변경된 상태 한 번 더 저장
        boolean isLikeONAfter = qnaService.isLikeON(id, username);

        // 좋아요 개수 저장
        int likeCnt = qnaService.getLikeCount(id);

        // 전송
        result.put("isLikeOn", isLikeONAfter);
        result.put("likeCount", likeCnt);

        return ResponseEntity.ok(result);
    }

    // 기존 파일 가져오기
    @GetMapping("/{boardId}/render-file")
    public ResponseEntity<Map<String, Object>> getOriginFile(@PathVariable("boardId") Long id) {

        Map<String, Object> result = new HashMap<>();

        List<MasterBoardFileVO> files = qnaService.getBoardFileById(id);

        for (MasterBoardFileVO f : files) {
            System.out.println(f.toString());
        }

        result.put("files", files);

        return ResponseEntity.ok(result);
    }

    // 게시글 보호 - 본인 아니면 수정삭제 숨김
    // 여기에서 가져올 것은 오직 아이디.
    @GetMapping("/{boardId}/check-id")
    public ResponseEntity<Map<String, Object>> btnManager(@PathVariable("boardId") Long id) {

        Map<String, Object> result = new HashMap<>();

        String username = getUsername();

        if (username == null) {
            result.put("isLikeOn", false);
            return ResponseEntity.ok(result);
        }

        boolean isOwner = username.equals(qnaService.getUsernameByBoardId(id));

        result.put("isOwner", isOwner);

        return ResponseEntity.ok(result);
    }

    // 공지 접근 제한 - 관리자 역할이 아니면 접근 차단
    // 역할을 가져와야 한다.
    @GetMapping("/check-role")
    public ResponseEntity<Map<String, Object>> isAdmin() {

        Map<String, Object> result = new HashMap<>();

        String username = getUsername();

        MemberVO memberVO = qnaService.getMemberInfo(username);

        String role = memberVO.getRole();

        if (role == null) {
            result.put("isAdmin", false);
            return ResponseEntity.ok(result);
        }

        boolean isAdmin = role.equals("ADMIN");

        result.put("isAdmin", isAdmin);

        return ResponseEntity.ok(result);
    }

    // 댓글 등록
    @PostMapping("/reply")
    public ResponseEntity<Map<String, Object>> insertReply(@RequestBody MasterBoardReplyVO masterBoardReplyVO){

        qnaService.insertReply(masterBoardReplyVO);

        return ResponseEntity.ok().build();
    }

    // 댓글 갱신
    @GetMapping("/reply/{boardId}")
    public ResponseEntity<Map<String, Object>> getReplyList(@PathVariable Long boardId) {


        Map<String, Object> result = new HashMap<>();

        List<CommunityReplyVO> replyList = qnaService.getReplyList(boardId);
        int replyCount = qnaService.getReplyCountByBoardId(boardId);

        result.put("reList", replyList);
        result.put("rCount", replyCount);

        return ResponseEntity.ok(result);
    }
    
        
    

}
