package com.miles.beauminity.controller.feed;

import java.security.Principal;

import java.util.List;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import com.miles.beauminity.service.feed.FeedService;
import com.miles.beauminity.vo.feed.FeedLikeVO;
import com.miles.beauminity.vo.feed.FeedReplyVO;
import com.miles.beauminity.vo.feed.FeedVO;

import lombok.AllArgsConstructor;


import org.springframework.ui.Model;


@Controller
@AllArgsConstructor
public class FeedController {
    private final FeedService feedService;

    // 피드 버튼 눌렀을때 화면 호출
    @GetMapping("/feed")
    public String getMainFeed(Model model) {

        // 작성된 피드 리스트로 가져와서 화면 띄우기
        List<FeedVO> feedList = feedService.getFeedList();
        // 저장되어있는 전체 태그 리스트로 가져와서 화면 띄우기
        List<String> tagList = feedService.getTagNameList();

        model.addAttribute("tagList", tagList);
        model.addAttribute("feedList", feedList);

        return "feed/list";
    }

    // 피드 작성하기 버튼 눌렀을 때 화면 호출
    @GetMapping("/feed/write")
    public String getWriteFeed() {
        return "feed/write";
    }

    // 피드 작성 후 등록 눌렀을 때 저장 및 화면 호출
    @PostMapping("/feed/write")
    public String postWriteFeed(
            @ModelAttribute FeedVO feedVO,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("tagNames") List<String> tagNames,
            Principal principal) {
        String username = principal.getName();
        feedVO.setUsername(username);
        // 피드 내용과 사진,해시태그 저장
        feedService.postFeed(feedVO, files, tagNames);
        return "redirect:/feed";
    }

    // 수정하기 눌렀을 때 화면 호출
    @GetMapping("/feed/{feedId}/edit")
    public String loadFeed(@PathVariable("feedId") Long feedId, Model model) {
        FeedVO feed = feedService.loadFeedData(feedId);
        model.addAttribute("feed", feed);
        return "feed/edit";
    }

    // 수정 완료 눌렀을 때
    @PostMapping("/feed/{feedId}/edit")
    public String updateFeed(@PathVariable("feedId") Long feedId,
            @ModelAttribute FeedVO feedVO,
            @RequestParam("files") MultipartFile[] files,
            @RequestParam("existingImages") List<String> existingImages, // 기존 사진
            @RequestParam("tagNames") List<String> tagNames) {

        feedVO.setFeedId(feedId);

        String content = feedVO.getFeedContent();

        // 내용이 null이라면 빈 문자열로 변환
        if (content == null) {
            content = "";
        }

        feedVO.setFeedContent(content.trim());
        
        feedService.updateFeed(feedId, feedVO, files, existingImages, tagNames);

        return "redirect:/feed";
    }

    // 삭제하기 눌렀을 때 화면 호출
    @GetMapping("/feed/{feedId}/delete")
    public String deleteFeedId(@PathVariable("feedId") Long feedId) {
        feedService.deleteFeedId(feedId);
        return "redirect:/feed";
    }

    // 작성한 댓글 DB에 저장
    @PostMapping("/feed/{feedId}/reply")
    @ResponseBody
    public List<FeedReplyVO> postReply(@PathVariable("feedId") Long feedId,
            FeedReplyVO feedReplyVO,
            @RequestParam(value = "parentsReplyId", required = false) Long parentsReplyId,
            Principal principal) {
        String username = principal.getName();
        feedReplyVO.setUsername(username);
        feedReplyVO.setFeedId(feedId);
        feedReplyVO.setParentsReplyId(parentsReplyId);
        return feedService.getFeedReply(feedReplyVO);
    }

    // 좋아요 눌렀을 때 저장 및 화면 반영
    @PostMapping("/feed/{feedId}/like")
    @ResponseBody // 메서드 반환 값을 http 응답 본문에 직접 담아 클라이언트 전송
    public int getFeedLike(@PathVariable("feedId") Long feedId,
            FeedLikeVO feedLikeVO,
            @RequestParam("liked") boolean liked,
            Principal principal) {
        String username = principal.getName();
        feedLikeVO.setUsername(username);
        feedLikeVO.setFeedId(feedId);

        // 좋아요 개수 가져오기
        int likeCnt = feedService.getFeedLike(liked, feedLikeVO);
        return likeCnt;
    }

    //본인 특정 댓글 수정
    @PostMapping("/feed/reply/{replyId}/edit")
    @ResponseBody
    public List<FeedReplyVO> updateReply(@PathVariable("replyId") Long replyId,
            String replyContent,
            FeedReplyVO feedReplyVO) {
        feedReplyVO.setReplyId(replyId);
        feedReplyVO.setReplyContent(replyContent);

        return feedService.updateReply(feedReplyVO);
    }

    //본인 특정 댓글 삭제
    @PostMapping("/feed/reply/{replyId}/delete")
    public String getMethodName(@PathVariable("replyId") Long replyId) {
        feedService.deleteReply(replyId);
        return "redirect:/feed/list";

    }

    //공유 링크로 들어 올 경우 -> 해당 피드 먼저 보기
    @GetMapping("/feed/{feedId}")
    public String shareFeedById(@PathVariable("feedId") Long feedId,Model model) {
        List<FeedVO> feedList = feedService.getShareFeedlist(feedId);
        model.addAttribute("feedList",feedList);
        return "feed/list";
    }

    //메인화면에서 피드 검색하기
    @GetMapping("/search/{tagName}")
    public String getMethodName(@PathVariable("tagName") String tagName,Model model) {
        List<FeedVO> feedList = feedService.getSearchTag(tagName);
        model.addAttribute("feedList",feedList);
        return "feed/list";
    }
    
}
