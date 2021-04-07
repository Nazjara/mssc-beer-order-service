package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.domain.BeerOrder;
import com.nazjara.beer.order.service.domain.BeerOrderEventEnum;
import com.nazjara.beer.order.service.domain.BeerOrderStatusEnum;
import com.nazjara.beer.order.service.repositories.BeerOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachineFactory;
    private final BeerOrderRepository beerOrderRepository;

    @Transactional
    @Override
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

        var savedBeerOrder = beerOrderRepository.save(beerOrder);

        sendBeerOrderEvent(savedBeerOrder, BeerOrderEventEnum.VALIDATE_ORDER);

        return savedBeerOrder;
    }

    private void sendBeerOrderEvent(BeerOrder beerOrder, BeerOrderEventEnum beerOrderEventEnum) {
        var stateMachine = build(beerOrder);

        var message = MessageBuilder.withPayload(beerOrderEventEnum).build();

        stateMachine.sendEvent(message);
    }

    private StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> build(BeerOrder beerOrder) {
        var stateMachine = stateMachineFactory.getStateMachine(beerOrder.getId());

        stateMachine.stop();

        stateMachine.getStateMachineAccessor()
                .doWithAllRegions(sma ->
                        sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus(),
                                null, null, null)));

        stateMachine.start();

        return stateMachine;
    }
}
