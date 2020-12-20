package com.sample.state.machine.config;

import com.sample.state.machine.domain.OrderEvent;
import com.sample.state.machine.domain.OrderState;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.weaver.ast.Or;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Slf4j
class StateMachineConfigTest {

    @Autowired
    private StateMachineFactory<OrderState, OrderEvent> factory;

    @Test
    void testAprobalPending() {
        StateMachine<OrderState, OrderEvent> sm = factory.getStateMachine(UUID.randomUUID());
        sm.start();
        log.info(sm.getState().toString());


        sm.sendEvent(OrderEvent.VERIFY_CONSUMER);
        log.info(sm.getState().toString());


        sm.sendEvent(OrderEvent.CONSUMER_VERIFIED);
        log.info(sm.getState().toString());

        sm.sendEvent(OrderEvent.TICKET_CREATED);
        log.info(sm.getState().toString());

        sm.sendEvent(OrderEvent.CARD_AUTHORIZED);
        log.info(sm.getState().toString());

        sm.sendEvent(OrderEvent.APPROVE_ORDER);
        log.info(sm.getState().toString());
    }
}