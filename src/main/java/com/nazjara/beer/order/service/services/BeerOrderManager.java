package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.domain.BeerOrder;

public interface BeerOrderManager {

    BeerOrder newBeerOrder(BeerOrder beerOrder);
}
