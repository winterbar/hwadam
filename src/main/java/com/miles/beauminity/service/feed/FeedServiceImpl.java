package com.miles.beauminity.service.feed;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.mapper.FeedFileMapper;
import com.miles.beauminity.mapper.FeedMapper;
import com.miles.beauminity.mapper.TagMapper;
import com.miles.beauminity.util.FileUploadUtil;
import com.miles.beauminity.vo.feed.FeedFileVO;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.feed.TagVO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FeedServiceImpl implements FeedService{
    private final FeedMapper feedMapper; 
    private final FeedFileMapper feedFileMapper;
    private final TagMapper tagMapper;

    
    @Override
    public void postFeed(FeedVO feedVO,MultipartFile[] files,List<String> tagNames) {
        //파일 저장
        feedMapper.postFeed(feedVO);
        Long feedId = feedVO.getFeedId();
        String uploadPath = "c:/upload";
        List<FeedFileVO> fileList = FileUploadUtil.saveFiles(files,uploadPath);
        for(FeedFileVO f:fileList) {
            f.setFeedId((feedId));
            feedFileMapper.postFile(f);
        }
        // 한개의 피드에 해시태그 여러개 등록할 수 있어 반복문 사용
        for(String tag:tagNames) {
            TagVO t = new TagVO();
            t.setTagName(tag);
            // 태그 저장
            tagMapper.postTag(t);
            Long tagId = t.getTagId();
            //피드태그 테이블에 피드 아이디 및 태그 저장
            tagMapper.tagFeed(feedId,tagId);
        } 

    }
    //태그 리스트 가져오기
    @Override
    public List<String> getTagNameList() {
        return tagMapper.getTagNameList();
    }
    //작성된 피드 가져오기
    @Override
    public List<String> getFeedList() {
        return feedMapper.getFeedList();
    }

}
