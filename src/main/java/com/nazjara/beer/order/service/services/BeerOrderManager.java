package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.domain.BeerOrder;
import com.nazjara.beer.order.service.model.BeerOrderDto;
import java.util.UUID;

public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);
    void processValidationResult(UUID id, boolean isValid);
    void processAllocationResult(BeerOrderDto beerOrderDto, boolean isAllocationError, boolean pendingInventory);
}
