package com.miles.beauminity.mapper.feed;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.feed.FeedFileVO;
import com.miles.beauminity.vo.feed.FeedVO;

@Mapper
public interface FeedFileMapper {
    public void postFile(FeedFileVO feedFileVO);

    public List<String> getFileList();

    public  List<String> getFeedFileList(Long feedId);

    public void deleteFile(FeedVO feedVO);

    public List<FeedFileVO> getFileListByFeedId(Long feedId);
}
