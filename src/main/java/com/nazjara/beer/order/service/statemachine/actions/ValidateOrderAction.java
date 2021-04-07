package com.nazjara.beer.order.service.statemachine.actions;

import com.nazjara.beer.order.service.domain.BeerOrderEventEnum;
import com.nazjara.beer.order.service.domain.BeerOrderStatusEnum;
import com.nazjara.beer.order.service.model.events.ValidateOrderRequest;
import com.nazjara.beer.order.service.repositories.BeerOrderRepository;
import com.nazjara.beer.order.service.web.mappers.BeerOrderMapper;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;
import static com.nazjara.beer.order.service.config.JmsConfig.VALIDATE_ORDER_QUEUE;
import static com.nazjara.beer.order.service.services.BeerOrderManagerImpl.BEER_ORDER_ID_HEADER;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> context) {
        var beerOrderId = (UUID) context.getMessage().getHeaders().get(BEER_ORDER_ID_HEADER);
        var beerOrder = beerOrderRepository.findOneById(beerOrderId);

        jmsTemplate.convertAndSend(VALIDATE_ORDER_QUEUE, ValidateOrderRequest.builder()
                .beerOrder(beerOrderMapper.beerOrderToDto(beerOrder))
                .build());

        log.debug("Sent validation request to the queue for order id: " + beerOrderId);
    }
}
