package com.example.orderservice.controller;

import com.example.orderservice.dto.OrderDto;
import com.example.orderservice.jpa.OrderEntity;
import com.example.orderservice.service.OrderService;
import com.example.orderservice.vo.RequestOrder;
import com.example.orderservice.vo.ResponseOrder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("order-service")
public class OrderController {
    private final Environment env;
    private final OrderService service;

    @GetMapping("/health-check")
    public String status(){
        return String.format("It's Working in Order Service on LOCAL PORT %s (SERVER PORT %s)",
                env.getProperty("local.server.port"),
                env.getProperty("server.port"));
    }

    @PostMapping("/{userId}/orders")
    public ResponseEntity<ResponseOrder> createOrder(@PathVariable("userId") String userId,
                                                     @RequestBody RequestOrder orderDetails){
        log.info("Before add orders data");
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);

        OrderDto orderDto = modelMapper.map(orderDetails, OrderDto.class);
        orderDto.setUserId(userId);

        OrderDto createDto = service.createOrder(orderDto);
        ResponseOrder returnValue = modelMapper.map(createDto, ResponseOrder.class);

        log.info("After added orders data");
        return ResponseEntity.status(HttpStatus.CREATED).body(returnValue);
    }

    @GetMapping("/{userId}/orders")
    public ResponseEntity<List<ResponseOrder>> getOrder(@PathVariable("userId") String userId){
        log.info("Before retrieve orders data");
        Iterable<OrderEntity> orderList = service.getOrdersByUserId(userId);
        List<ResponseOrder> result = new ArrayList<>();

        orderList.forEach(v->{
            result.add(new ModelMapper().map(v, ResponseOrder.class));
        });

        log.info("Add retrieved orders data");
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }
}
