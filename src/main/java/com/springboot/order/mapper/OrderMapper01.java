package com.springboot.order.mapper;

import com.springboot.coffee.service.CoffeeService;
import com.springboot.member.service.MemberService;
import com.springboot.order.dto.*;
import com.springboot.order.entity.Order;
import com.springboot.order.entity.OrderCoffee;
import com.springboot.order.service.OrderService;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderMapper01 implements OrderMapper{
    MemberService memberService;
    CoffeeService coffeeService;
    OrderService orderService;

    public OrderMapper01(MemberService memberService, CoffeeService coffeeService, OrderService orderService) {
        this.memberService = memberService;
        this.coffeeService = coffeeService;
        this.orderService = orderService;
    }

    //List<OrderCoffeeDto> ->  List<OrderCoffee> 로 매핑
    //post에서메서드로 사용, 그 내부에서 생성한 Order를 파람으로 받음 , 아직 Order 생성전
    public OrderCoffee orderCoffeeDtoToOrderCoffee(OrderCoffeeDto dto, Order order) {
        OrderCoffee orderCoffee = new OrderCoffee();
        //coffeeId
        orderCoffee.setCoffee(coffeeService.findVerifiedCoffee(dto.getCoffeeId()));
        orderCoffee.setQuantity(dto.getQuantity());
        orderCoffee.setOrder(order);

        return orderCoffee;

    }

    @Override
    public Order orderPostDtoToOrder(OrderPostDto dto) {
        Order order = new Order();
        order.setOrderCoffees(dto.getOrderCoffees().stream().map(orderCoffeeDto ->
                        orderCoffeeDtoToOrderCoffee(orderCoffeeDto, order))
                .collect(Collectors.toList()));
        order.setMember(memberService.findVerifiedMember(dto.getMemberId()));


        return order;
    }

    @Override
    public Order orderPatchDtoToOrder(OrderPatchDto orderPatchDto) {
        return null;
    }

    @Override
    public OrderResponseDto orderToOrderResponseDto(Order order) {
       OrderResponseDto dto = new OrderResponseDto();
        dto.setOrderId(order.getOrderId());
        dto.setMemberId(order.getMember().getMemberId());
        dto.setOrderStatus(order.getOrderStatus());
        dto.setOrderCoffees(order.getOrderCoffees().stream().map(orderCoffee ->
                orderCoffeeToOrderCoffeeResponseDto(orderCoffee)).collect(Collectors.toList()));
        dto.setCreatedAt(order.getCreatedAt());

        return dto;
    }

    @Override
    public List<OrderResponseDto> ordersToOrderResponseDtos(List<Order> orders) {
        return orders.stream().map(order -> orderToOrderResponseDto(order)).collect(Collectors.toList());
    }

    public OrderCoffeeResponseDto orderCoffeeToOrderCoffeeResponseDto(OrderCoffee orderCoffee) {

        return  new OrderCoffeeResponseDto(
                orderCoffee.getCoffee().getCoffeeId(),
                orderCoffee.getCoffee().getKorName(),
                orderCoffee.getCoffee().getEngName(),
                orderCoffee.getCoffee().getPrice(),
                orderCoffee.getQuantity()
        );

    }
}
