package com.sample.state.machine.services;

import com.sample.state.machine.domain.Order;
import com.sample.state.machine.domain.OrderState;
import com.sample.state.machine.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class OrderServiceImplTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository repository;


    Order order;

    @BeforeEach
    void setUp(){
        order = Order.builder().idConsumer(123L).cardNumber("1234523452344678").build();
    }

    @Test
    @Transactional
    void newOrder() {
        Order savedOrder = orderService.newOrder(order);
        Order verifyConsumerOrder = repository.getOne(savedOrder.getId());
        assertEquals(OrderState.NEW.name(), verifyConsumerOrder.getState().name());
    }

    @Test
    @Transactional
    void verifyConsumer() {
        Order savedOrder = orderService.newOrder(order);
        orderService.verifyConsumer(order);
        Order verifyConsumerOrder = repository.getOne(savedOrder.getId());
        assertEquals(OrderState.VERIFYING_CONSUMER.name(), verifyConsumerOrder.getState().name());
    }

    @Test
    @Transactional
    void createTicket() {
        Order savedOrder = orderService.newOrder(order);
        order.setState(OrderState.VERIFYING_CONSUMER);
        orderService.createTicket(order);
        Order verifyConsumerOrder = repository.getOne(savedOrder.getId());
        assertEquals(OrderState.CREATING_TICKET.name(), verifyConsumerOrder.getState().name());
    }

    @Test
    @Transactional
    void authorizeCard() {
        Order savedOrder = orderService.newOrder(order);
        order.setState(OrderState.CREATING_TICKET);
        orderService.authorizeCard(order);
        Order verifyConsumerOrder = repository.getOne(savedOrder.getId());
        assertEquals(OrderState.AUTHORIZING_CARD.name(), verifyConsumerOrder.getState().name());
    }

    @Test
    @Transactional
    void approveTicket() {
        Order savedOrder = orderService.newOrder(order);
        order.setState(OrderState.CARD_AUTHORIZED);
        orderService.approveOrder(order);
        Order verifyConsumerOrder = repository.getOne(savedOrder.getId());
        assertEquals(OrderState.ORDER_APPROVED.name(), verifyConsumerOrder.getState().name());
    }

    @Test
    @Transactional
    void rejectTicket() {
        Order savedOrder = orderService.newOrder(order);
        order.setState(OrderState.VERIFYING_CONSUMER);
        orderService.rejectOrder(order);
        Order verifyConsumerOrder = repository.getOne(savedOrder.getId());
        assertEquals(OrderState.ORDER_REJECTED.name(), verifyConsumerOrder.getState().name());
    }

}