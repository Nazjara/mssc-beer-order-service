package com.nazjara.beer.order.service.services;

import com.nazjara.beer.order.service.domain.BeerOrder;
import com.nazjara.beer.order.service.domain.BeerOrderStatusEnum;
import com.nazjara.beer.order.service.model.BeerOrderDto;
import com.nazjara.beer.order.service.model.BeerOrderPagedList;
import com.nazjara.beer.order.service.repositories.BeerOrderRepository;
import com.nazjara.beer.order.service.repositories.CustomerRepository;
import com.nazjara.beer.order.service.web.mappers.BeerOrderMapper;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class BeerOrderServiceImpl implements BeerOrderService {

    private final BeerOrderRepository beerOrderRepository;
    private final CustomerRepository customerRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final BeerOrderManager beerOrderManager;

    @Override
    public BeerOrderPagedList listOrders(UUID customerId, Pageable pageable) {
        var customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            Page<BeerOrder> beerOrderPage =
                    beerOrderRepository.findAllByCustomer(customerOptional.get(), pageable);

            return new BeerOrderPagedList(beerOrderPage
                    .stream()
                    .map(beerOrderMapper::beerOrderToDto)
                    .collect(Collectors.toList()), PageRequest.of(
                    beerOrderPage.getPageable().getPageNumber(),
                    beerOrderPage.getPageable().getPageSize()),
                    beerOrderPage.getTotalElements());
        } else {
            return null;
        }
    }

    @Transactional
    @Override
    public BeerOrderDto placeOrder(UUID customerId, BeerOrderDto beerOrderDto) {
        var customerOptional = customerRepository.findById(customerId);

        if (customerOptional.isPresent()) {
            var beerOrder = beerOrderMapper.dtoToBeerOrder(beerOrderDto);
            beerOrder.setId(null); //should not be set by outside client
            beerOrder.setCustomer(customerOptional.get());
            beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);

            beerOrder.getBeerOrderLines().forEach(line -> line.setBeerOrder(beerOrder));

            var savedBeerOrder = beerOrderManager.newBeerOrder(beerOrder);

            return beerOrderMapper.beerOrderToDto(savedBeerOrder);
        }
        //todo add exception type
        throw new RuntimeException("Customer Not Found");
    }

    @Override
    public BeerOrderDto getOrderById(UUID customerId, UUID orderId) {
        return beerOrderMapper.beerOrderToDto(getOrder(customerId, orderId));
    }

    @Override
    public void pickupOrder(UUID customerId, UUID orderId) {
        var beerOrder = getOrder(customerId, orderId);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.PICKED_UP);

        beerOrderManager.beerOrderPickedUp(orderId);
    }

    private BeerOrder getOrder(UUID customerId, UUID orderId){
        var customerOptional = customerRepository.findById(customerId);

        if(customerOptional.isPresent()){
            var beerOrderOptional = beerOrderRepository.findById(orderId);

            if(beerOrderOptional.isPresent()){
                var beerOrder = beerOrderOptional.get();

                // fall to exception if customer id's do not match - order not for customer
                if (beerOrder.getCustomer().getId().equals(customerId)){
                    return beerOrder;
                }
            }
            throw new RuntimeException("Beer Order Not Found");
        }
        throw new RuntimeException("Customer Not Found");
    }
}
