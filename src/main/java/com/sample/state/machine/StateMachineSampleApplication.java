package com.sample.state.machine;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.statemachine.config.EnableStateMachine;

@SpringBootApplication
public class StateMachineSampleApplication {

	public static void main(String[] args) {
		SpringApplication.run(StateMachineSampleApplication.class, args);
	}

}
