package com.sample.state.machine.domain;

/**
 * Created by martin.saporiti
 * on 19/12/2020
 * Github: https://github.com/martinsaporiti
 */

public enum OrderState {

    NEW, VERIFYING_CONSUMER, CREATING_TICKET, AUTHORIZING_CARD, CARD_AUTHORIZED, CARD_DECLINED, ORDER_APPROVED, ORDER_REJECTED;

}
