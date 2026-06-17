package com.miles.beauminity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.miles.beauminity.vo.feed.TagVO;

@Mapper
public interface TagMapper {
    void postTag(TagVO tagNames);
    List<String> getTagNameList();
    void tagFeed(@Param("feedId")Long getFeedId,@Param("tagId") Long getTagId);
}
