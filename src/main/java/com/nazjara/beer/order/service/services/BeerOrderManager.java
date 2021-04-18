package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.domain.BeerOrder;

import java.util.UUID;

public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);
    void processValidationResult(UUID id, boolean isValid);
}
