package com.miles.beauminity.controller.qna_board;

import java.io.File;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.miles.beauminity.service.qna_board.QnaService;
import com.miles.beauminity.vo.MasterBoardFileVO;
import com.miles.beauminity.vo.MasterBoardVO;

import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;




@Controller
@AllArgsConstructor
public class QnaController {
    // 서비스를 받아옵니다.
    private final QnaService qnaService;

    // 질문 게시판 이동
    // 여기에서 목록을 받아와야할 거 같은데...
    // 파라미터에 값 받아오는 느낌인가 으음 
    @GetMapping("/board/qna")
    public String getQnaList(Model model) {

        // 파라미터 여러개는 페이징 적용 이후에 고려해보는 걸로. 
        String type = "qna";

        List<MasterBoardVO> qnaBoardList=qnaService.getTypeBoard(type);

        for(MasterBoardVO q : qnaBoardList){
            System.out.println(q.toString());
        }
        // 일단 

        model.addAttribute("qnaS", qnaBoardList);

        return "qna_board/qna";
    }

    // 질문 게시글 작성
    @GetMapping("/board/qna/write")
    public String getQnaWrite() {
        return "qna_board/write";
    }

    // 질문 게시글을 포스트
    @PostMapping("/board/qna")
    public String postQna(@ModelAttribute MasterBoardVO masterBoardVO, @RequestParam("files") MultipartFile[] files) {
        
        masterBoardVO.setBoardType("qna");

        // 회원 등록이 아직 없기 때문에 임시로 적용.

        // 0617 - 파일 메타데이터 저장 추가, 확인을 위해 순차적 출력 시도.
        for(MultipartFile f:files){
            System.out.println(f.getOriginalFilename());
            System.out.println(f.getSize());
        }


        // vo에 모든 값이 잘 들어갔나? 테스트 해봅니다.
        System.out.println(masterBoardVO.toString());

        // 모든 값이 잘 들어가는 것을 확인했기 때문에 이제 서비스의 메서드로 넘기겠습니다.
        qnaService.insertBoard(masterBoardVO, files);
        
        return "redirect:/board/qna";
    }

    //게시글 이동 링크
    @GetMapping("/board/qna/{id}")
    public String getQnaDetail(@PathVariable("id") Long id, Model model) {

        MasterBoardVO qna = qnaService.getOneBoard(id);
        List<MasterBoardFileVO> qnaFiles = qnaService.getBoardById(id);

        qnaService.viewUp(id);

        System.out.println(qna.toString());

        model.addAttribute("qna", qna);
        model.addAttribute("flist", qnaFiles);


        return "qna_board/detail";
    }

    // 게시글 삭제 링크
    @GetMapping("/board/qna/delete/{id}")
    public String getQnaDel(@PathVariable("id") Long id) {

        // id 잘 나오나 체크
        System.out.println(id);

        qnaService.deleteBoard(id);

        return "redirect:/board/qna";
    }

    // 게시글 수정 링크
    @GetMapping("/board/qna/edit/{id}")
    public String getQnaEdit(@PathVariable("id") Long id, Model model) {

        // 일단 수정을 할 게시글부터 가져옵니다. 겟 원보드 나와라.

        MasterBoardVO qnaEdit = qnaService.getOneBoard(id);

        model.addAttribute("qnaEdit", qnaEdit);

        System.out.println("테스트: "+qnaEdit.toString());

        
        return "/qna_board/edit";
    }

    //수정한 거... 보낸다~!
    @PostMapping("/board/qna/update")
    public String postMethodName(@ModelAttribute MasterBoardVO masterBoardVO) {
        
        // 모든 값이 잘 고쳐졌나 볼게요 
        System.out.println(masterBoardVO.toString());

        qnaService.updateBoard(masterBoardVO);
        
        return "redirect:/board/qna";
    }

    // 파일 첨부 메서드
    @ResponseBody
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downLoadFile(@PathVariable("filename") String filename) {
        String uploadDir = "C:\\uploads";
        File file = new File(uploadDir, filename);

        if (!file.exists() || !file.isFile()) {
            return ResponseEntity.notFound().build();
        }
        Resource resource = new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=\"" + filename + "\"")
                .body(resource);
    }
    

    
    
    
    
}
