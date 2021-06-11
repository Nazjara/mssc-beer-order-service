package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.domain.BeerOrder;
import com.nazjara.beer.order.service.domain.BeerOrderLine;
import com.nazjara.beer.order.service.domain.BeerOrderStatusEnum;
import com.nazjara.beer.order.service.domain.Customer;
import com.nazjara.beer.order.service.repositories.BeerOrderRepository;
import com.nazjara.beer.order.service.repositories.CustomerRepository;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class BeerOrderManagerIT {

    @Autowired
    BeerOrderManager beerOrderManager;

    @Autowired
    BeerOrderRepository beerOrderRepository;

    @Autowired
    CustomerRepository customerRepository;

    Customer customer;
    UUID beerId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        customer = customerRepository.save(Customer.builder()
                .customerName("Test customer")
                .build());
    }

    @Test
    void testNewToAllocate() {
        var beerOrder = createBeerOrder();

        var savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

        assertNotNull(savedBeerOrder);
        assertEquals(BeerOrderStatusEnum.ALLOCATED, savedBeerOrder.getOrderStatus());
    }

    public BeerOrder createBeerOrder() {
        var beerOrder = BeerOrder.builder()
                .customer(customer)
                .build();

        var beerOrderLine = BeerOrderLine.builder()
                .beerId(beerId)
                .orderQuantity(1)
                .beerOrder(beerOrder)
                .build();

        beerOrder.setBeerOrderLines(Set.of(beerOrderLine));

        return beerOrder;
    }
}
