package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.domain.BeerOrder;
import com.nazjara.beer.order.service.domain.BeerOrderEventEnum;
import com.nazjara.beer.order.service.domain.BeerOrderStatusEnum;
import com.nazjara.beer.order.service.repositories.BeerOrderRepository;
import com.nazjara.beer.order.service.statemachine.BeerOrderStateChangeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static com.nazjara.beer.order.service.domain.BeerOrderEventEnum.VALIDATION_FAILED;
import static com.nazjara.beer.order.service.domain.BeerOrderEventEnum.VALIDATION_PASSED;

@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    public static final String BEER_ORDER_ID_HEADER = "order_id";

    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderStateChangeInterceptor interceptor;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

        var savedBeerOrder = beerOrderRepository.save(beerOrder);

        sendBeerOrderEvent(savedBeerOrder, BeerOrderEventEnum.VALIDATE_ORDER);

        return savedBeerOrder;
    }

    @Override
    public void processValidationResult(UUID id, boolean isValid) {
        var beerOrder = beerOrderRepository.getOne(id);

        if (isValid) {
            sendBeerOrderEvent(beerOrder, VALIDATION_PASSED);
        } else {
            sendBeerOrderEvent(beerOrder, VALIDATION_FAILED);
        }
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum beerOrderEventEnum) {
        var stateMachine = build(beerOrder);

        var message = MessageBuilder
                .withPayload(beerOrderEventEnum)
                .setHeader(BEER_ORDER_ID_HEADER, beerOrder.getId())
                .build();

        stateMachine.sendEvent(message);
    }

    private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder beerOrder) {
        var stateMachine = stateMachineFactory.getStateMachine(beerOrder.getId());

        stateMachine.stop();

        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                            sma.addStateMachineInterceptor(interceptor);
                            sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(),
                                    null, null, null));
                });

        stateMachine.start();

        return stateMachine;
    }
}
