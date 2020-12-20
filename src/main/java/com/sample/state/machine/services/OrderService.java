package com.sample.state.machine.services;

import com.sample.state.machine.domain.Order;
import com.sample.state.machine.domain.OrderEvent;
import com.sample.state.machine.domain.OrderState;
import org.springframework.statemachine.StateMachine;

/**
 * Created by martin.saporiti
 * on 19/12/2020
 * Github: https://github.com/martinsaporiti
 */

public interface OrderService {

    Order newOrder(Order order);

    StateMachine<OrderState, OrderEvent> verifyConsumer(Order order);

    StateMachine<OrderState, OrderEvent> createTicket(Order order);

    StateMachine<OrderState, OrderEvent> authorizeCard(Order order);

    StateMachine<OrderState, OrderEvent> approveOrder(Order order);


}
