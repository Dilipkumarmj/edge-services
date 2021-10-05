package com.example.edge;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;

@Controller
public class CrmGraphqlController {

    private final CrmClient client;

    public CrmGraphqlController(CrmClient client){
        this.client = client;
    }

    @SchemaMapping(typeName = "Customer")
    Flux<CrmClient.Order> orders(CrmClient.Customer customer){
        return this.client.getOrdersFor(customer.id());
    }
    @QueryMapping
    Flux<CrmClient.Customer> customers(){
        return this.client.getCustomers();
    }
}
