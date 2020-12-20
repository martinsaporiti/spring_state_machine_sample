package com.sample.state.machine.services;

import com.sample.state.machine.domain.Order;
import com.sample.state.machine.domain.OrderEvent;
import com.sample.state.machine.domain.OrderState;
import com.sample.state.machine.repository.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

/**
 * Created by martin.saporiti
 * on 19/12/2020
 * Github: https://github.com/martinsaporiti
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    public static final String ORDER_ID_HEADER = "order_id";

    private final OrderRepository repository;
    private final StateMachineFactory<OrderState, OrderEvent> factory;
    private final OrderStateChangeInterceptor orderStateChangeInterceptor;

    @Override
    @Transactional
    public Order newOrder(Order order) {
        order.setState(OrderState.NEW);
        return repository.save(order);
    }

    @Override
    @Transactional
    public StateMachine<OrderState, OrderEvent> verifyConsumer(Order order) {
        StateMachine<OrderState, OrderEvent> sm = build(order.getId());
        sendEvent(order.getId(), sm, OrderEvent.VERIFY_CONSUMER);
        return null;
    }

    @Override
    @Transactional
    public StateMachine<OrderState, OrderEvent> createTicket(Order order) {
        StateMachine<OrderState, OrderEvent> sm = build(order.getId());
        sendEvent(order.getId(), sm, OrderEvent.CREATE_TICKET);
        return null;
    }

    @Override
    @Transactional
    public StateMachine<OrderState, OrderEvent> authorizeCard(Order order) {
        StateMachine<OrderState, OrderEvent> sm = build(order.getId());
        sendEvent(order.getId(), sm, OrderEvent.AUTHORIZE_CARD);
        return null;
    }

    @Override
    @Transactional
    public StateMachine<OrderState, OrderEvent> approveOrder(Order order) {
        StateMachine<OrderState, OrderEvent> sm = build(order.getId());
        sendEvent(order.getId(), sm, OrderEvent.APPROVE_ORDER);
        return null;
    }

    @Override
    public StateMachine<OrderState, OrderEvent> rejectOrder(Order order) {
        StateMachine<OrderState, OrderEvent> sm = build(order.getId());
        sendEvent(order.getId(), sm, OrderEvent.ORDER_REJECTED);
        return null;
    }


    /**
     *
     * @param orderId
     * @return StateMachine<OrderState, OrderEvent>
     */
    private StateMachine<OrderState, OrderEvent> build(Long orderId){
        Order order = repository.getOne(orderId);
        StateMachine<OrderState, OrderEvent> sm = factory.getStateMachine(Long.toString(orderId));
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(orderStateChangeInterceptor);
                    sma.resetStateMachine(
                            new DefaultStateMachineContext<>(order.getState(), null, null, null));
                });
        sm.start();
        return sm;
    }

    /**
     *
     * @param orderId
     * @param sm
     * @param event
     */
    private void sendEvent(Long orderId, StateMachine<OrderState, OrderEvent> sm, OrderEvent event){
        Message msg = MessageBuilder.withPayload(event)
                .setHeader(ORDER_ID_HEADER, orderId)
                .build();
        sm.sendEvent(msg);
    }
}
