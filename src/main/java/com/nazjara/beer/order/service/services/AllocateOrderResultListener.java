package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.config.JmsConfig;
import com.nazjara.beer.order.service.model.events.AllocateOrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class AllocateOrderResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(AllocateOrderResult result) {
        beerOrderManager.processAllocationResult(result.getBeerOrder(), result.isAllocationError(), result.isPendingInventory());
    }
}
