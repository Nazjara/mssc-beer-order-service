package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.config.JmsConfig;
import com.nazjara.beer.order.service.model.events.ValidateOrderResult;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class BeerOrderValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE_QUEUE)
    public void listen(ValidateOrderResult result) {
        beerOrderManager.processValidationResult(result.getOrderId(), result.getIsValid());
    }
}
