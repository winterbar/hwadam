package com.miles.beauminity.service.feed;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.mapper.feed.FeedFileMapper;
import com.miles.beauminity.mapper.feed.FeedMapper;
import com.miles.beauminity.mapper.feed.FeedReplyMapper;
import com.miles.beauminity.mapper.feed.TagMapper;
import com.miles.beauminity.util.FileUploadUtil;
import com.miles.beauminity.vo.feed.FeedFileVO;
import com.miles.beauminity.vo.feed.FeedLikeVO;
import com.miles.beauminity.vo.feed.FeedReplyVO;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.feed.TagVO;
import com.miles.beauminity.vo.login.MemberVO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FeedServiceImpl implements FeedService {
    private final FeedMapper feedMapper;
    private final FeedFileMapper feedFileMapper;
    private final TagMapper tagMapper;
    private final FeedReplyMapper feedReplyMapper;

    @Override
    public void postFeed(FeedVO feedVO, MultipartFile[] files, List<String> tagNames) {
        // 파일 저장
        feedMapper.postFeed(feedVO);
        Long feedId = feedVO.getFeedId();
        String uploadPath = "c:/upload";
        List<FeedFileVO> fileList = FileUploadUtil.saveFiles(files, uploadPath);
        for (FeedFileVO f : fileList) {
            f.setFeedId((feedId));
            feedFileMapper.postFile(f);
        }
        // 한개의 피드에 해시태그 여러개 등록할 수 있어 반복문 사용
        for (String tag : tagNames) {
            TagVO t = new TagVO();
            t.setTagName(tag);
            // 태그 저장
            tagMapper.postTag(t);
            Long tagId = t.getTagId();
            // 피드태그 테이블에 피드 아이디 및 태그 저장
            tagMapper.tagFeed(feedId, tagId);
        }

    }

    // 태그 리스트 가져오기
    @Override
    public List<String> getTagNameList() {
        return tagMapper.getTagNameList();
    }

    // 작성된 피드 가져오기
    @Override
    public List<FeedVO> getFeedList(String username) {
        List<FeedVO> feedList = feedMapper.getFeedList(username);
        // 특정 피드에 작성된 해시태그 가져오기
        // 피드 전체 길이 만큼 반복
        for (FeedVO feed : feedList) {
            // 특정 피드 아이디에 해당하는 해시태그를 가져와서 리스트로 저장
            List<String> feedTagList = tagMapper.getFeedTagList(feed.getFeedId());
            // 특정 피드 아이디에 해당하는 사진 가져와서 리스트로 저장
            List<String> feedFileList = feedFileMapper.getFeedFileList(feed.getFeedId());
            List<String> feedReplyList = feedReplyMapper.getFeedReplyList(feed.getFeedId());

            feed.setFeedFileList(feedFileList);
            feed.setFeedTagList(feedTagList);
            feed.setFeedReplyList(feedReplyList);
            feed.setReplyCnt(feedReplyList.size());

        }

        return feedList;
    }

    @Override
    public FeedVO loadFeedData(Long feedId) {
        FeedVO feed = feedMapper.loadFeedData(feedId);
        List<String> feedTagList = tagMapper.getFeedTagList(feedId);
        // 특정 피드 아이디에 해당하는 사진 가져와서 리스트로 저장
        List<String> feedFileList = feedFileMapper.getFeedFileList(feedId);
        feed.setFeedFileList(feedFileList);
        feed.setFeedTagList(feedTagList);
        return feed;
    }

    @Override
    public void deleteFeedId(Long feedId) {
        feedMapper.deleteFeedId(feedId);
        tagMapper.deleteTag(feedId);
    }

    @Override
    @Transactional
    public void updateFeed(Long feedId, FeedVO feedVO,
            MultipartFile[] files,
            List<String> existingImages,
            List<String> tagNames) {

        // 피드 내용 수정
        feedMapper.updateFeed(feedVO);

        // 기존 파일 목록 조회
        List<FeedFileVO> oldFileList = feedFileMapper.getFileListByFeedId(feedId);

        // DB에서 기존 파일 전체 삭제
        feedFileMapper.deleteFile(feedVO);

        // 사용자가 삭제하지 않고 남긴 기존 사진만 다시 저장
        if (existingImages != null) {
            for (FeedFileVO oldFile : oldFileList) {
                if (existingImages.contains(oldFile.getSavedName())) {
                    oldFile.setFileId(null);
                    oldFile.setFeedId(feedId);
                    feedFileMapper.postFile(oldFile);
                }
            }
        }

        // 새로 추가한 사진 저장
        if (files != null) {
            String uploadPath = "c:/upload";

            List<FeedFileVO> fileList = FileUploadUtil.saveFiles(files, uploadPath);

            for (FeedFileVO f : fileList) {
                f.setFeedId(feedId);
                feedFileMapper.postFile(f);
            }
        }

        // 태그는 기존 연결 삭제 후 다시 등록
        tagMapper.deleteTag(feedId);

        if (tagNames != null) {
            for (String tag : tagNames) {
                if (tag == null || tag.trim().isEmpty())
                    continue;

                TagVO t = new TagVO();
                t.setTagName(tag.trim());

                tagMapper.postTag(t);

                Long tagId = t.getTagId();

                tagMapper.tagFeed(feedId, tagId);
            }
        }
    }

    @Override
    public int getFeedLike(Boolean liked, FeedLikeVO feedLikeVO) {
        if (liked) {
            // 좋아요 누르고 한번 더 누를 경우
            feedMapper.cancleLikeCnt(feedLikeVO);
        } else {
            // 좋아요 한번 눌렀을 경우
            feedMapper.increaseLikeCnt(feedLikeVO);
        }
        return feedMapper.getLikeCnt(feedLikeVO);

    }

    @Override
    public List<FeedReplyVO> getFeedReply(FeedReplyVO feedReplyVO) {
        feedReplyMapper.postReply(feedReplyVO);
        return feedReplyMapper.getReply(feedReplyVO);
    }

}
