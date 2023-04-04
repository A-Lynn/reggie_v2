package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;

/**
 * 文件的上传、下载、保存路径
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    /**
     * 文件保存路径
     */
    @Value("${reggie.path}")
    private String basePath;

    /**
     * 文件上传
     * @param file
     * @return
     */
    @PostMapping("/upload")
    public R<String> upload(MultipartFile file){
        //file是临时文件，需要转存到指定位置，否则本次请求完成后临时文件会删除
        log.info(file.toString());
        //获取原始文件名
        String originalFilename = file.getOriginalFilename();
        //截取原始文件后缀名
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        //使用UUID重新生成文件名，防止文件名称重复造成覆盖
        String fileName = UUID.randomUUID().toString() + suffix;
        //创建目录对象
        File dir = new File(basePath);
        //没有目录就创建
        if(!dir.exists()){
            //目录不存在，需要创建
            dir.mkdirs();
        }

        try{
            //文件存放路径
            file.transferTo(new File(basePath + fileName));
        }catch(IOException e){
            e.printStackTrace();
        }

        return R.success(fileName);
    }

    /**
     * 文件下载
     * @param name
     * @param response
     */
    @GetMapping("/download")
    public void download(String name, HttpServletResponse response) throws IOException {
        //通过输入流读取文件内容
        FileInputStream fileInputStream = new FileInputStream(new File(basePath + name));
        //通过输出流将文件写回浏览器，在浏览器展示图片,返回浏览器需要response请求
        ServletOutputStream outputStream = response.getOutputStream();
        //设置文件类型jpg
        response.setContentType("image/jpeg");

        int len = 0;
        byte[] bytes = new byte[1024];
        while((len = fileInputStream.read(bytes)) != -1){
            outputStream.write(bytes, 0, len);
        }
        //关闭流
        outputStream.close();
        fileInputStream.close();
    }



}
