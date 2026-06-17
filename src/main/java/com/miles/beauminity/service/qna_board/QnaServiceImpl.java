package com.miles.beauminity.service.qna_board;

import java.util.List;

import org.springframework.stereotype.Service;

import com.miles.beauminity.mapper.MasterBoardMapper;
import com.miles.beauminity.vo.MasterBoardVO;
import lombok.AllArgsConstructor;

// 질문게시판 구현체입니다.
// 서비스는 구현체에 다는 거다...

@Service
@AllArgsConstructor
public class QnaServiceImpl implements QnaService {

    // 매퍼를 객체로 불러와줍니다.
    private MasterBoardMapper masterBoardMapper;

    // 게시글 등록 
    @Override
    public void insertBoard(MasterBoardVO masterBoardVO) {
        masterBoardMapper.insertBoard(masterBoardVO);
    }

    // 게시글 전체조회
    @Override
    public List<MasterBoardVO> getTypeBoard(String type) {

        System.out.println(type);

        return masterBoardMapper.getTypeBoard(type);
    }

    // 게시글 상세조회
    @Override
    public MasterBoardVO getOneBoard(Long id) {

        return masterBoardMapper.getOneBoard(id);
    }

    // 게시글 삭제
    @Override
    public void deleteBoard(Long id) {
        masterBoardMapper.deleteBoard(id);
    }



    

    

}
