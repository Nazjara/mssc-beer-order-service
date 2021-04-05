package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.web.model.BeerDto;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Optional<BeerDto> getBeerById(UUID uuid);
    Optional<BeerDto> getBeerByUpc(String upc);
}
