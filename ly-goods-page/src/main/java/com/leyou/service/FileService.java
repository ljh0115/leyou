package com.leyou.service;

import com.leyou.util.ThreadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@Service
public class FileService {

    @Autowired
    private LyGoodsPageService lyGoodsPageService;

    @Autowired
    private TemplateEngine templateEngine;

    @Value("${ly.thymeleaf.destPath}")
    private String destPath;

    /**
     * 判断某个商品的页面是否存在
     * @param id
     * @return
     */
    public Boolean exist(Long id){
        File file = new File(destPath);
        if(!file.exists()){
            file.mkdirs();
        }
        File file1 = new File(destPath,id+".html");
        return file1.exists();
    }

    /*启动线程，创建本地html*/
    public void syncCreateHtml(Long id){
        ThreadUtils.execute(()->{
            try {
                createHtml(id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void createHtml(Long id) {

        //创建全局上下文
         Context context = new Context();
         //存入数据到context
         context.setVariables(lyGoodsPageService.toProductDetail(id));
         //准备文件路径
        File filePath = new File(destPath,id+".html");
        try {
            PrintWriter printWriter = new PrintWriter(filePath,"UTF-8");
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void deleteHtml(Long id){
        File file = new File(destPath, id + ".html");
        file.delete();
    }
}
