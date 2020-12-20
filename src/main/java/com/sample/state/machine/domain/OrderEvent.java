package com.sample.state.machine.domain;

/**
 *
 * Created by martin.saporiti
 * on 19/12/2020
 * Github: https://github.com/martinsaporiti
 */
public enum OrderEvent {
    VERIFY_CONSUMER, CONSUMER_VERIFIED, CREATE_TICKET, TICKET_CREATED, AUTHORIZE_CARD, CARD_AUTHORIZED, CARD_DECLINED,
    APPROVE_TICKET, APPROVE_ORDER;
}
