package com.sample.state.machine.repository;

import com.sample.state.machine.domain.Order;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by martin.saporiti
 * on 19/12/2020
 * Github: https://github.com/martinsaporiti
 */
public interface OrderRepository extends JpaRepository<Order, Long> {
}
