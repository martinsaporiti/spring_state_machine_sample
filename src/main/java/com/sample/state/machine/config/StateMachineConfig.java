package com.sample.state.machine.config;

import com.sample.state.machine.domain.Order;
import com.sample.state.machine.domain.OrderEvent;
import com.sample.state.machine.domain.OrderState;
import com.sample.state.machine.services.OrderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachine;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigBuilder;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.transition.Transition;

import java.util.EnumSet;
import java.util.Random;

/**
 * This class contains the state machine configuration. It defines the transitions between events.
 * Rules:
 * 1 The saga orchestrator sends a Verify Consumer command to Consumer Service.
 * 2 Consumer Service replies with a Consumer Verified message.
 * 3 The saga orchestrator sends a Create Ticket command to Kitchen Service.
 * 4 Kitchen Service replies with a Ticket Created message.
 * 5 The saga orchestrator sends an Authorize Card message to Accounting Service.
 * 6 Accounting Service replies with a Card Authorized message.
 * 7 The saga orchestrator sends an Approve Ticket command to Kitchen Service.
 * 8 The saga orchestrator sends an Approve Order command to Order Service.
 *
 * Created by martin.saporiti
 * on 19/12/2020
 * Github: https://github.com/martinsaporiti
 */
@Slf4j
@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<OrderState, OrderEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<OrderState, OrderEvent> states) throws Exception {
        states.withStates()
                .initial(OrderState.NEW)
                .states(EnumSet.allOf(OrderState.class))
                .end(OrderState.ORDER_APPROVED)
                .end(OrderState.ORDER_REJECTED)
                .end(OrderState.CARD_DECLINED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderState, OrderEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(OrderState.NEW)
                .target(OrderState.VERIFYING_CONSUMER)
                .event(OrderEvent.VERIFY_CONSUMER)
                .and()
                .withExternal()
                .source(OrderState.VERIFYING_CONSUMER)
                .target(OrderState.CREATING_TICKET)
                .event(OrderEvent.CREATE_TICKET)
                .and()
                .withExternal()
                .source(OrderState.VERIFYING_CONSUMER)
                .target(OrderState.ORDER_REJECTED)
                .event(OrderEvent.ORDER_REJECTED)
                .and()
                .withExternal()
                .source(OrderState.CREATING_TICKET)
                .target(OrderState.AUTHORIZING_CARD)
                .event(OrderEvent.AUTHORIZE_CARD)
                .action(authorizeCardAction())
                .and()
                .withExternal()
                .source(OrderState.CREATING_TICKET)
                .target(OrderState.CARD_AUTHORIZED)
                .event(OrderEvent.CARD_AUTHORIZED)
                .and()
                .withExternal()
                .source(OrderState.CREATING_TICKET)
                .target(OrderState.CARD_DECLINED)
                .event(OrderEvent.CARD_DECLINED)
                .and()
                .withExternal()
                .source(OrderState.CARD_AUTHORIZED)
                .target(OrderState.ORDER_APPROVED)
                .event(OrderEvent.APPROVE_ORDER);

    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<OrderState, OrderEvent> config) throws Exception {
        StateMachineListenerAdapter<OrderState, OrderEvent> adapter = new StateMachineListenerAdapter<>(){

            @Override
            public void stateChanged(State<OrderState, OrderEvent> from, State<OrderState, OrderEvent> to) {
                log.info(String.format("stateChanged(from: %s, to: %s)", from, to));
            }

        };

        config.withConfiguration().listener(adapter);
    }

    /**
     *
     * @return Action<OrderState, OrderEvent>
     */
    public Action<OrderState, OrderEvent> authorizeCardAction(){

        return context -> {
            System.out.println("Authorize Card was called!!!");
            System.out.println(context.getStateMachine().getState());
            System.out.println(context.getEvent());

            if (new Random().nextInt(10) < 8) {
                System.out.println("Approved");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(OrderEvent.CARD_AUTHORIZED)
                        .setHeader(OrderServiceImpl.ORDER_ID_HEADER,
                                context.getMessageHeader(OrderServiceImpl.ORDER_ID_HEADER))
                        .build());

            } else {
                System.out.println("Declined! No Credit!!!!!!");
                context.getStateMachine().sendEvent(MessageBuilder.withPayload(OrderEvent.CARD_DECLINED)
                        .setHeader(OrderServiceImpl.ORDER_ID_HEADER,
                                context.getMessageHeader(OrderServiceImpl.ORDER_ID_HEADER))
                        .build());
            }
        };
    }
}
