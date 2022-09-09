package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.messagequeue.KafkaProducer;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order-service")
public class OrderController {

    Environment env;
    OrderService orderService;
    KafkaProducer kafkaProducer;

    @Autowired
    public OrderController(Environment env, OrderService orderService, KafkaProducer kafkaProducer) {
        this.env = env;
        this.orderService = orderService;
        this.kafkaProducer = kafkaProducer;
    }

    @GetMapping("/health_check")
    public String status() {
        return String.format("It's Working in Order Service on PORT %s", env.getProperty("local.server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable String userId, @RequestBody RequestOrder orderDetails) {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        /* jpa */
        OrderDto orderDto = mapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);
        final OrderDto orderResult = orderService.createOrder(orderDto);

        ResponseOrder responseOrder = mapper.map(orderResult, ResponseOrder.class);

        /* send this order to the kafka */
        /* catalog service kafka listener 에 등록해 놓은 topic 이름 */
        kafkaProducer.send("example-catalog-topic", orderDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseOrder);
    }

    @GetMapping("/orders/{orderId}")
    public ResponseEntity<ResponseOrder> getOrderByOrderId(@PathVariable String orderId) {
        final OrderDto order = orderService.getOrderByOrderId(orderId);
        ResponseOrder result = new ModelMapper().map(order, ResponseOrder.class);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrdersByUserId(@PathVariable String userId) {
        final List<OrderEntity> orders = orderService.getOrdersByUserId(userId);

        List<ResponseOrder> results = new ArrayList<>();

        orders.forEach(o -> {
            results.add(new ModelMapper().map(o, ResponseOrder.class));
        });

        return ResponseEntity.status(HttpStatus.OK).body(results);
    }

}
