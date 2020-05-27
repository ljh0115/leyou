package com.leyou.item.pojo;

import lombok.Data;

import javax.persistence.Transient;

@Data
public class SpuBo extends Spu {

   
    @Transient
    private String cname;// 商品分类名称

    @Transient
    private String bname;// 品牌名称
    

}