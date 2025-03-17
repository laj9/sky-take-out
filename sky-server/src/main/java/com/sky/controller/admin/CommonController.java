package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.service.Tags;

import java.io.IOException;
import java.util.UUID;

/**
 * ClassName: CommonController
 * Package: com.sky.controller.admin
 * Description:
 *
 * @Author Aijing Liu
 * @Create 2025/3/16 21:29
 * @Version 1.0
 */
@RestController
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil aliOssUtil;

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("开始上传文件：{}", file);
        try {
            //获取文件后缀
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            //生成随机名称
            String newName = UUID.randomUUID().toString() + extension;

            //上传
            String filePath = aliOssUtil.upload(file.getBytes(), newName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.error("上传文件出错：{}" + e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }
}
