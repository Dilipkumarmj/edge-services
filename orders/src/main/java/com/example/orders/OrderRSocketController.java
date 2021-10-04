package com.example.orders;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Controller
public class OrderRSocketController {

    record Order(@JsonProperty("id") Integer id,@JsonProperty("customerId") Integer customerId){}

    private final Map<Integer, Collection<Order>> db = new ConcurrentHashMap<>();

    OrderRSocketController(){
        for(var customerId=1;customerId<=8;customerId++){
            var orders = new ArrayList<Order>();
            for(var orderId=1;orderId<=(Math.random()*100);orderId++){
                orders.add(new Order(orderId,customerId));
            }
            this.db.put(customerId,orders);
        }
    }

    @MessageMapping("orders.{cid}")
    Flux<Order> ordersFor(@DestinationVariable Integer cid){
        return Flux.fromIterable(this.db.get(cid));
    }
}
