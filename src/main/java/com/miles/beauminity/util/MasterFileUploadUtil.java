package com.miles.beauminity.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.web.multipart.MultipartFile;

import com.miles.beauminity.vo.board.MasterBoardFileVO;

public class MasterFileUploadUtil {
    public static List<MasterBoardFileVO> saveFiles(
        MultipartFile[] files,
        String uploadPath) {
            List<MasterBoardFileVO> fileList = new ArrayList<>();
            File folder = new File(uploadPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            for(MultipartFile file :files) {
                if(file.isEmpty()) {
                    continue;
                }
                try {
                    String originalName =file.getOriginalFilename();
                    String uuid = UUID.randomUUID().toString();
                    String savedName = uuid+"_"+originalName;
                    File saveFile = new File(uploadPath
                                    +File.separator
                                    +savedName);
                    file.transferTo(saveFile);

                    MasterBoardFileVO fileVO = new MasterBoardFileVO();
                    fileVO.setOriginalName(originalName);
                    fileVO.setSavedName(savedName);
                    fileVO.setFilePath(uploadPath);
                    fileVO.setFileType("image/png");
                    fileVO.setFileSize((int)file.getSize());

                    fileList.add(fileVO);

                }catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return fileList;
        }
}
