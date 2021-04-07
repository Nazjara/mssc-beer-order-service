package com.nazjara.beer.order.service.domain;

public enum BeerOrderStatusEnum {
    NEW,
    VALIDATED,
    VALIDATION_PENDING,
    VALIDATION_EXCEPTION,
    ALLOCATION_PENDING,
    ALLOCATED,
    ALLOCATION_EXCEPTION,
    PENDING_INVENTORY,
    PICKED_UP,
    DELIVERED,
    DELIVERY_EXCEPTION
}
