package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.domain.BeerOrder;
import com.nazjara.beer.order.service.model.BeerOrderDto;
import java.util.UUID;

public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);
    void processValidationResult(UUID beerOrderId, Boolean isValid);
    void beerOrderAllocationPassed(BeerOrderDto beerOrder);
    void beerOrderAllocationPendingInventory(BeerOrderDto beerOrder);
    void beerOrderAllocationFailed(BeerOrderDto beerOrder);
    void beerOrderPickedUp(UUID id);
    void cancelOrder(UUID id);
}
