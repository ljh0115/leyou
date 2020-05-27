package com.leyou.utils;

import java.util.List;

public class PageResult<T> {
    private Long total;//总条数
    private Long totalPage;//总页数
    private List<T> items;//当前页数据

    public PageResult() {
    }

    public PageResult(Long total,List<T> items){
      this.total=total;
      this.items=items;
      //两个参数的构造 总条数和当前页
    }

    public PageResult(Long total, Long totalPage, List<T> items) {
        this.total = total;
        this.totalPage = totalPage;
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Long totalPage) {
        this.totalPage = totalPage;
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }
}
