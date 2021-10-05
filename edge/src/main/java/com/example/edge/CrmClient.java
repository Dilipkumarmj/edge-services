package com.example.edge;

import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class CrmClient {
    record Customer (Integer id,String name){}
    record Order(Integer id,Integer customerId){}
    record CustomerOrders(Customer customer, List<Order> orders){}
    private final WebClient http;

    private final RSocketRequester rSocket;

    public CrmClient(WebClient http,RSocketRequester rSocket){
        this.http = http;
        this.rSocket = rSocket;
    }

    Flux<Order> getOrdersFor(Integer customerId){
        return this.rSocket.route("orders.{cid}",customerId).retrieveFlux(Order.class);
    }

    Flux<CustomerOrders> getCustomerOrders(){
        return this.getCustomers().flatMap(customer -> Mono.zip(
                Mono.just(customer),
                getOrdersFor(customer.id()).collectList()
        )).map(tuple2 -> new CustomerOrders(tuple2.getT1(),tuple2.getT2()));
    }

    Flux<Customer> getCustomers(){
        return this.http.get().uri("http://localhost:8080/customers").retrieve().bodyToFlux(Customer.class);
    }

}
