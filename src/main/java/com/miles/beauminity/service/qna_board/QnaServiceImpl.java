package com.miles.beauminity.service.qna_board;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.mapper.MasterBoardFileMapper;
import com.miles.beauminity.mapper.MasterBoardMapper;
import com.miles.beauminity.util.MasterFileUploadUtil;
import com.miles.beauminity.vo.MasterBoardFileVO;
import com.miles.beauminity.vo.MasterBoardVO;
import com.miles.beauminity.vo.PageVO;
import com.miles.beauminity.vo.TypeOffsetVO;

import lombok.AllArgsConstructor;

// 질문게시판 구현체입니다.
// 서비스는 구현체에 다는 거다...

@Service
@AllArgsConstructor
public class QnaServiceImpl implements QnaService {

    // 매퍼를 객체로 불러와줍니다.
    private MasterBoardMapper masterBoardMapper;
    private MasterBoardFileMapper masterBoardFileMapper;

    // 게시글 등록 
    @Override
    public void insertBoard(MasterBoardVO masterBoardVO, MultipartFile[] files) {
        
        // 일단 게시글부터 게시.
        System.out.println("전:" + masterBoardVO.getBoardId());
        masterBoardMapper.insertBoard(masterBoardVO);
        System.out.println("후:" + masterBoardVO.getBoardId());

        // 번호가 잘 들어가는 걸 확인했으니 이제 파일 정보랑 글 번호를 합쳐서 DB에 올려놓아야한다. 
        // VO가 필요하겠다. vo 만들러 가자
        // 오늘은 게시만 하고 집 가겠다 수정은 내일 와서 해야지..

        Long boardId = masterBoardVO.getBoardId();
        String uploadPath = "c:/uploads";
        List<MasterBoardFileVO> fileList = MasterFileUploadUtil.saveFiles(files, uploadPath);
        for(MasterBoardFileVO f: fileList){
            f.setBoardId(boardId);

            // 매퍼 적용
            masterBoardFileMapper.insertFile(f);
        }

        //
    }

    // 게시글 전체조회
    @Override
    public List<MasterBoardVO> getTypeBoard(String type, PageVO pageVO) {

        System.out.println(type);

        TypeOffsetVO typeOffsetVO = new TypeOffsetVO();

        typeOffsetVO.setType(type);
        typeOffsetVO.setOffset(pageVO.getOffset());
        typeOffsetVO.setSize(pageVO.getSize());

        return masterBoardMapper.getTypeBoard(typeOffsetVO);
    }

    // 게시글 상세조회
    @Override
    public MasterBoardVO getOneBoard(Long id) {

        System.out.println(masterBoardMapper.getOneBoard(id).toString());

        return masterBoardMapper.getOneBoard(id);
    }

    // 게시글 상세조회 시 파일 조회
    @Override
    public List<MasterBoardFileVO> getBoardFileById(Long id) {
        return masterBoardFileMapper.getBoardFileById(id);
    }

    // 게시글 삭제
    @Override
    public void deleteBoard(Long id) {
        masterBoardMapper.deleteBoard(id);
    }

    // 조회수 증가
    @Override
    public void viewUp(Long id) {
        masterBoardMapper.viewUp(id);
    }

    // 게시글 수정
    @Override
    public void updateBoard(MasterBoardVO masterBoardVO) {
        masterBoardMapper.updateBoard(masterBoardVO);
    }

    

    



    

    

}
