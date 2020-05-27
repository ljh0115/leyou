package com.leyou.search.controller;

import com.leyou.entity.SearchResult;
import com.leyou.service.SearchService;
import com.leyou.utils.SearchRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchControl {

    @Autowired
    private SearchService searchService;

    /*  @PostMapping("page")
      public ResponseEntity<PageResult<Goods>> search(@RequestBody SearchRequest searchRequest){
          PageResult<Goods> search = searchService.search(searchRequest);
          if(search==null&&search.getItems()==null){
              return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
          }
          return ResponseEntity.ok(search);
      }*/
    @PostMapping("page")
    public ResponseEntity<SearchResult> search(@RequestBody SearchRequest searchRequest) {
        SearchResult search = searchService.search(searchRequest);
        if (search == null && search.getItems() == null) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(search);
    }

}
