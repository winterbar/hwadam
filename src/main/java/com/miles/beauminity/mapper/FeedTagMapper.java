package com.miles.beauminity.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.miles.beauminity.vo.TagVO;

@Mapper
public interface FeedTagMapper {
    void postTag(List<TagVO> tagNames);
}
