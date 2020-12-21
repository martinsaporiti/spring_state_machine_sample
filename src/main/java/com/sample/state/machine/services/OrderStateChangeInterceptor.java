package com.sample.state.machine.services;

import com.sample.state.machine.domain.Order;
import com.sample.state.machine.domain.OrderEvent;
import com.sample.state.machine.domain.OrderState;
import com.sample.state.machine.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * This interceptor "handle" the state machine transitions.
 * Created by martin.saporiti
 * on 19/12/2020
 * Github: https://github.com/martinsaporiti
 */
@RequiredArgsConstructor
@Component
public class OrderStateChangeInterceptor extends StateMachineInterceptorAdapter<OrderState, OrderEvent> {

    private final OrderRepository repository;

    @Override
    public void preStateChange(State<OrderState, OrderEvent> state, Message<OrderEvent> message,
                               Transition<OrderState, OrderEvent> transition,
                               StateMachine<OrderState, OrderEvent> stateMachine) {

        Optional.ofNullable(message).ifPresent(msg -> {
            Optional.ofNullable(Long.class.cast(msg.getHeaders().getOrDefault(OrderServiceImpl.ORDER_ID_HEADER, -1L)))
                    .ifPresent(orderId -> {
                        Order order = repository.getOne(orderId);
                        order.setState(state.getId());
                        repository.save(order);
                    });
        });

    }

}
