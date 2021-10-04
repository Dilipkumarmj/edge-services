package com.example.customers;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Controller
@ResponseBody
public class CustomersRestController {

    private final AtomicInteger id = new AtomicInteger();

    record Customer(@JsonProperty("id") Integer id,@JsonProperty("name") String name){}

    private final List<Customer> customers = List.of("Mario", "Zhouyue", "Zhen", "Mia", "Stéphane", "Valerie", "Mike", "Julia")
            .stream().map(name -> new Customer(this.id.incrementAndGet(),name)).collect(Collectors.toList());

    @GetMapping("/customers")
    Flux<Customer> get(){
        return Flux.fromIterable(this.customers);
    }
}
