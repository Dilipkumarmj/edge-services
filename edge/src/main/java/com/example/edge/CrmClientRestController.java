package com.example.edge;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Flux;

@Controller
@ResponseBody
public class CrmClientRestController {

    private final CrmClient crm;

    CrmClientRestController(CrmClient crm){
        this.crm = crm;
    }

    @GetMapping("/cos")
    Flux<CrmClient.CustomerOrders> get(){
        return this.crm.getCustomerOrders();
    }

}
