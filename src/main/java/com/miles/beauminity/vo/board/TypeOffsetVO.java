package com.miles.beauminity.vo.board;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class TypeOffsetVO {
    private int size;
    private int offset;
    private String type;
    private String category = "";
}
