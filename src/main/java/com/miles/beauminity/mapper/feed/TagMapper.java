package com.miles.beauminity.mapper.feed;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.feed.TagVO;

@Mapper
public interface TagMapper {
    public void postTag(TagVO tagNames);

    public List<String> getTagNameList();

    public List<String> getFeedTagList(Long feedId);

    public void tagFeed(@Param("feedId") Long getFeedId, @Param("tagId") Long getTagId);

    public List<String> getfeedTagList();

    public void deleteTag(Long feedId);

    public void deleteFeedTag(FeedVO feedVO);

    public List<FeedVO> getTopTagList();

    public List<FeedVO> getSearchTag(String tagName);

}
