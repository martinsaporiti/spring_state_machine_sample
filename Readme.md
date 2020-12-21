# Spring State Machine Sample

![example workflow file path](https://github.com/martinsaporiti/spring_state_machine_sample/.github/workflows/maven.yml/badge.svg)

## Introduction
This prototype shows how to set up a state machine using Spring.

States Machines are useful when we implement Orchestration-based Sagas in Microservices.

## Context
I've implemented the following state machine based on one example of "Microservices Patterns by  Chris Richardson's" book.
1. The saga orchestrator sends a Verify Consumer command to Consumer Service.
2. Consumer Service replies with a Consumer Verified message.
3. The saga orchestrator sends a Create Ticket command to Kitchen Service.
4. Kitchen Service replies with a Ticket Created message.
5. The saga orchestrator sends an Authorize Card message to Accounting Service.
6. Accounting Service replies with a Card Authorized message.
7. The saga orchestrator sends an Approve Ticket command to Kitchen Service.
8. The saga orchestrator sends an Approve Order command to Order Service.

![state machine](https://res.cloudinary.com/dmg0wwwhg/image/upload/v1608506875/zpyfkti8uhaug9qvdirm.png)

