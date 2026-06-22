package com.miles.beauminity.util;

import java.io.File;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.login.MyPageFileVO;

public class MemberFileUtil {
    // 프로필 사진 저장
    public static MyPageFileVO saveFiles(MultipartFile file, String uploadPath) {
        // 파일이 등록되지 않았거나 비어있는 파일인지 확인
        if(file == null || file.isEmpty()) {
            return null;
        }
        
        // 파일을 저장할 폴더, 없으면 새로 생성
        File folder = new File(uploadPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        
        // 실제로 파일 등록 후 등록한 파일의 정보를 MyPageFileVO 객체로 반환
        try {
            String originalName =file.getOriginalFilename();
            String uuid = UUID.randomUUID().toString();
            String savedName = uuid + "_" + originalName;
            File saveFile = new File(uploadPath + File.separator + savedName);
            file.transferTo(saveFile);

            MyPageFileVO fileVO = new MyPageFileVO();
            fileVO.setOriginalName(originalName);
            fileVO.setSavedName(savedName);
            fileVO.setFilePath(uploadPath);
            fileVO.setFileType(file.getContentType());
            fileVO.setFileSize((int)file.getSize());

            return fileVO;

        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // 프로필 사진 삭제
    public static void deleteFiles(String uploadPath, String savedName) {
        File file = new File(uploadPath + File.separator + savedName);
        // 지정한 경로에 저장된 파일이 있는지 확인
        // 저장되어 있다면 해당 파일을 삭제
        if(file.exists()) {
            file.delete();
        }
    }
}
