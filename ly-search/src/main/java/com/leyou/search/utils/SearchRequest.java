package com.leyou.search.utils;

import java.util.Map;

//前台发送请求，搜索类
public class SearchRequest {

    private String key;
    private Integer page;
    private Map<String, Object> filter;

    private static final Integer DEFAULT_PAGE = 1;
    private static final Integer DEFAULT_SIZE = 20;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Integer getPage() {
        if (page == null) {
            return DEFAULT_PAGE;
        }
        return Math.max(page, DEFAULT_PAGE);
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Map<String, Object> getFilter() {
        return filter;
    }

    public void setFilter(Map<String, Object> filter) {
        this.filter = filter;
    }

    public Integer getDefaultSize() {
        return DEFAULT_SIZE;
    }
}
