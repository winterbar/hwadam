package com.miles.beauminity.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.board.MasterBoardFileVO;

public class MasterFileUtil {
    public static List<MasterBoardFileVO> saveFiles(
            MultipartFile[] files,
            String uploadPath) {
        List<MasterBoardFileVO> fileList = new ArrayList<>();
        File folder = new File(uploadPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                continue;
            }
            try {
                String originalName = file.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                String savedName = uuid + "_" + originalName;
                File saveFile = new File(uploadPath
                        + File.separator
                        + savedName);
                file.transferTo(saveFile);

                MasterBoardFileVO fileVO = new MasterBoardFileVO();
                fileVO.setOriginalName(originalName);
                fileVO.setSavedName(savedName);
                fileVO.setFilePath(uploadPath);
                fileVO.setFileType("image/png");
                fileVO.setFileSize((int) file.getSize());

                fileList.add(fileVO);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return fileList;
    }

    public static void deleteFiles(String uploadPath, String savedName) {
        File file = new File(uploadPath + File.separator + savedName);
        // 지정한 경로에 저장된 파일이 있는지 확인
        // 저장되어 있다면 해당 파일을 삭제
        if (file.exists()) {
            file.delete();
        }
    }

    public static void copyFiles(String prevPath, String savedName, String nextPath){
        File file = new File(prevPath, savedName);
	    File targetFolder = new File(nextPath);
        File newFile = new File(nextPath, savedName);

        if (!file.exists() || !file.isFile()) {
            System.err.println("파일이 없습니다");
            return;
        }

        try {
            // 3. 예외 방지: 목적지 폴더가 없으면 생성
            if (!targetFolder.exists()) {
                targetFolder.mkdirs();
            }

            // 4. 파일 복사 실행 (기존 파일이 있다면 덮어쓰기)
            Files.copy(file.toPath(), newFile
            .toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("파일 복사 완료");

        } catch (IOException e) {
            e.printStackTrace();
        }
	
    }
}
