package com.miles.beauminity.service.feed;

import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUpload;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.mapper.FeedFileMapper;
import com.miles.beauminity.mapper.FeedMapper;
import com.miles.beauminity.mapper.FeedTagMapper;
import com.miles.beauminity.util.FileUploadUtil;
import com.miles.beauminity.vo.feed.FeedFileVO;
import com.miles.beauminity.vo.feed.FeedTagVO;
import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.feed.TagVO;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FeedServiceImpl implements FeedService{
    private final FeedMapper feedMapper; 
    private final FeedFileMapper feedFileMapper;
    private final FeedTagMapper feedTagMapper;
    
    @Override
    public void postFeed(FeedVO feedVO,MultipartFile[] files,List<TagVO> tagNames) {
        feedMapper.postFeed(feedVO);
        Long feedId = feedVO.getFeedId();
        String uploadPath = "c:/upload";
        List<FeedFileVO> fileList = FileUploadUtil.saveFiles(files,uploadPath);
        for(FeedFileVO f:fileList) {
            f.setFeedId((feedId));
            feedFileMapper.postFile(f);
        }
        for(TagVO t:tagNames) {
            feedTagMapper.postTag(tagNames);
        }
        
    }
    
    
}
