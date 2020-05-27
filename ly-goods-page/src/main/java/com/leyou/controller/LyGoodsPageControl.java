package com.leyou.controller;

import com.leyou.service.FileService;
import com.leyou.service.LyGoodsPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Map;

@Controller
public class LyGoodsPageControl {

    @Autowired
    private LyGoodsPageService lyGoodsPageService;

    @Autowired
    private FileService fileService;

    @GetMapping("item/{spuId}.html")
    public String toProductDetail(@PathVariable("spuId") Long spuId , Model model){
        Map<String,Object> map = lyGoodsPageService.toProductDetail(spuId);
        model.addAllAttributes(map);
        if(!fileService.exist(spuId)){
            fileService.syncCreateHtml(spuId);
        }
        return "item";
    }

}
