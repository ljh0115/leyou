package com.leyou.search.listener;

import com.leyou.client.GoodsClient;
import com.leyou.mapper.GoodsMapper;
import com.leyou.service.IndexService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {

    @Autowired
    private IndexService indexService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue1",durable = "true"),
            exchange = @Exchange(value = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.update","item.inset"}
    ))
    public void createAndUpdateIndex(Long id){
           indexService.createAndUpdateIndex(id);
    }

    /*
    *   @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "queue2",durable = "true"),
            exchange = @Exchange(value = "ly.item.exchange",type = ExchangeTypes.TOPIC),
            key = {"item.delete"}
    ))
    public void createAndUpdateIndex(Long id){
           indexService.deleteIndex(id);
    }*/
}
