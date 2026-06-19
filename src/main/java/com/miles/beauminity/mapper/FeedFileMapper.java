package com.miles.beauminity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.feed.FeedFileVO;
import com.miles.beauminity.vo.feed.FeedVO;

@Mapper
public interface FeedFileMapper {
    void postFile(FeedFileVO feedFileVO);

    List<String> getFileList();

    List<String> getFeedFileList(Long feedId);
}
