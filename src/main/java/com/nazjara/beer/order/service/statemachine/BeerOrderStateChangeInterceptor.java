package com.nazjara.beer.order.service.statemachine;

import com.nazjara.beer.order.service.domain.BeerOrderEventEnum;
import com.nazjara.beer.order.service.domain.BeerOrderStatusEnum;
import com.nazjara.beer.order.service.repositories.BeerOrderRepository;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;
import static com.nazjara.beer.order.service.services.BeerOrderManagerImpl.BEER_ORDER_ID_HEADER;

@Service
@RequiredArgsConstructor
public class BeerOrderStateChangeInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {

    private final BeerOrderRepository beerOrderRepository;

    @Override
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEventEnum> state,
                               Message<BeerOrderEventEnum> message,
                               Transition<BeerOrderStatusEnum, BeerOrderEventEnum> transition,
                               StateMachine<BeerOrderStatusEnum, BeerOrderEventEnum> stateMachine) {
        Optional.ofNullable(message).flatMap(msg -> Optional.ofNullable((UUID) msg.getHeaders().get(BEER_ORDER_ID_HEADER))).ifPresent(id -> {
            var beerOrder = beerOrderRepository.getOne(id);
            beerOrder.setOrderStatus(state.getId());
            beerOrderRepository.saveAndFlush(beerOrder);
        });
    }
}
