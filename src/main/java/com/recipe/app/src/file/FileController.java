package com.recipe.app.src.file;

import com.recipe.app.src.common.aop.LoginCheck;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/file")
public class FileController {

    private final S3FileService s3FileService;

    public FileController(S3FileService s3FileService) {
        this.s3FileService = s3FileService;
    }

    @PostMapping("/upload")
    @LoginCheck
    public String uploadFile(@RequestPart("file")MultipartFile file) throws IOException {

        return s3FileService.uploadFile(file);
    }
}
