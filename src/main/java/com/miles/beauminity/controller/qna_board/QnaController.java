package com.miles.beauminity.controller.qna_board;

import java.io.File;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import com.miles.beauminity.service.qna_board.QnaService;
import com.miles.beauminity.vo.board.MasterBoardFileVO;
import com.miles.beauminity.vo.board.MasterBoardVO;
import com.miles.beauminity.vo.board.PageVO;
import com.miles.beauminity.vo.qna_board.QnaBoardCompleteVO;

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

    public String getUsername(){
    
        // 멤버 정보 가져오기
        SecurityContext context = SecurityContextHolder.getContext();
        Authentication authentication = context.getAuthentication();
        String username = authentication.getName();

        System.out.println("현재 로그인한 회원: "+username);
        
        return username;
    }


    // 질문 게시판 이동
    // 여기에서 목록을 받아와야할 거 같은데...
    // 파라미터에 값 받아오는 느낌인가 으음 
    @GetMapping("/community")
    public String getQnaList(@ModelAttribute PageVO pageVO, Model model) {

        LocalDate today = LocalDate.now();

        // 게시판 종류 설정
        String type = "qna";

        System.out.println("page: "+pageVO.getPage());
        System.out.println("offset: "+pageVO.getOffset());

        // 전체 게시글수 확인
        int count = qnaService.getTypeBoardCount(type); 
        System.out.println("게시글의 수:" + count);
        pageVO.pageInfo(count);

        // 파라미터 여러개는 페이징 적용 이후에 고려해보는 걸로. 
        List<QnaBoardCompleteVO> qnaBoardList=qnaService.getTypeBoard(type, pageVO);

        for(QnaBoardCompleteVO q : qnaBoardList )
            System.out.println(q.toString());

        model.addAttribute("qnaS", qnaBoardList);
        model.addAttribute("pageVO", pageVO);
        model.addAttribute("today", today);

        return "qna_board/list";
    }

    // 질문 게시글 작성
    @GetMapping("/community/write")
    public String getQnaWrite() {

        if (getUsername().equals("anonymousUser"))
            return "redirect:/login";

        return "qna_board/write";
    }

    // 질문 게시글을 포스트
    @PostMapping("/community")
    public String postQna(@ModelAttribute MasterBoardVO masterBoardVO
        , @RequestParam("category") String category, @RequestParam("files") MultipartFile[] files) {
        
        System.out.println("카테고리: "+category);
        masterBoardVO.setBoardType("qna");

        // 로그인 중인 회원의 아이디를 저장 
        masterBoardVO.setUsername(getUsername());

        // 0617 - 파일 메타데이터 저장 추가, 확인을 위해 순차적 출력 시도.
        for(MultipartFile f:files){
            System.out.println(f.getOriginalFilename());
            System.out.println(f.getSize());
        }


        // vo에 모든 값이 잘 들어갔나? 테스트 해봅니다.
        System.out.println(masterBoardVO.toString());

        // 모든 값이 잘 들어가는 것을 확인했기 때문에 이제 서비스의 메서드로 넘기겠습니다. 한꺼번에 넘기는 것으로.
        qnaService.insertBoard(masterBoardVO, files, category);
        
        return "redirect:/community";
    }

    //게시글 이동 링크
    @GetMapping("/community/{id}")
    public String getQnaDetail(@PathVariable("id") Long id, Model model) {

        MasterBoardVO qna = qnaService.getOneBoard(id);
        List<MasterBoardFileVO> qnaFiles = qnaService.getBoardFileById(id);

        qnaService.viewUp(id);

        System.out.println(qna.toString());

        String nickname = qnaService.getNicknameByBoardId(id);

        model.addAttribute("qna", qna);
        model.addAttribute("flist", qnaFiles);
        model.addAttribute("nickname", nickname);
        model.addAttribute("likecnt", qnaService.getLikeCount(id));


        return "qna_board/detail";
    }

    // 게시글 삭제 링크
    @GetMapping("/community/delete/{id}")
    public String getQnaDel(@PathVariable("id") Long id) {

        // id 잘 나오나 체크
        System.out.println(id);

        qnaService.deleteBoard(id);

        return "redirect:/community";
    }

    // 게시글 수정 링크
    @GetMapping("/community/edit/{id}")
    public String getQnaEdit(@PathVariable("id") Long id, Model model) {

        if (getUsername().equals("anonymousUser"))
            return "redirect:/login";

        // 일단 수정을 할 게시글부터 가져옵니다. 겟 원보드 나와라.

        MasterBoardVO qnaEdit = qnaService.getOneBoard(id);

        // 수정하는 거에 사진도 같이 넣으려면 어떻게 해야하나 
        List<MasterBoardFileVO> files = qnaService.getBoardFileById(id);

        model.addAttribute("qnaEdit", qnaEdit);
        model.addAttribute("qnaFiles", files);

        System.out.println("테스트: "+qnaEdit.toString());

        
        return "/qna_board/edit";
    }

    //수정한 거... 보낸다~!
    @PostMapping("/community/update")
    public String postMethodName(@ModelAttribute MasterBoardVO masterBoardVO,
                                 @RequestParam(value = "deletedFileIds", required = false) List<Long> deletedFileIds,
                                 @RequestParam("selectedFiles") MultipartFile[] files,
                                 String category
    ) {
        
        // 모든 값이 잘 고쳐졌나 볼게요 
        System.out.println(masterBoardVO.toString());

        if (deletedFileIds != null) {
            for (Long fileId : deletedFileIds){
                qnaService.deleteFilesForUpdate(fileId); 
            }
        }

        qnaService.updateBoard(masterBoardVO, files, category);
        
        return "redirect:/community";
    }

    // 파일 첨부 메서드
    @ResponseBody
    @GetMapping("/download/{filename}")
    public ResponseEntity<Resource> downLoadFile(@PathVariable("filename") String filename) {
        String uploadDir = "c:/uploads/qna";
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

    // 게시글 검색 메서드
    @PostMapping("/community/search")
    public String getSearchBoard(@RequestParam("searchStr") String str, @ModelAttribute PageVO pageVO, Model model) {
        
        System.out.println("현재 검색어: "+str);

        // 게시판 종류 설정
        String type = "qna";

        // 전체 게시글수 확인
        int count = qnaService.getCountSearchBoardByTitle(type, str); 
        System.out.println("게시글의 수:" + count);
        pageVO.pageInfo(count);

        // 파라미터 여러개는 페이징 적용 이후에 고려해보는 걸로. 
        List<QnaBoardCompleteVO> qnaBoardList=qnaService.getSearchBoard(type, str, pageVO);

        for(QnaBoardCompleteVO q : qnaBoardList )
            System.out.println(q.toString());

        model.addAttribute("qnaS", qnaBoardList);
        model.addAttribute("pageVO", pageVO);

        
        return "/qna_board/list";
    }
    
    

    
    
    
    
}
