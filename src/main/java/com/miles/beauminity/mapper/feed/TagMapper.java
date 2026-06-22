package com.miles.beauminity.mapper.feed;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.miles.beauminity.vo.feed.FeedVO;
import com.miles.beauminity.vo.feed.TagVO;

@Mapper
public interface TagMapper {
    void postTag(TagVO tagNames);

    List<String> getTagNameList();

    List<String> getFeedTagList(Long feedId);

    void tagFeed(@Param("feedId") Long getFeedId, @Param("tagId") Long getTagId);

    List<String> getfeedTagList();

    void deleteTag(Long feedId);

    void deleteFeedTag(FeedVO feedVO);

}
