package com.example.oa.mapper;

import com.example.oa.dto.OrderResponse;
import com.example.oa.entity.Order;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-31T16:07:53+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Microsoft)"
)
@Component
public class OrderMapperImpl implements OrderMapper {

    @Override
    public OrderResponse toResponse(Order order) {
        if ( order == null ) {
            return null;
        }

        OrderResponse orderResponse = new OrderResponse();

        orderResponse.setId( order.getId() );
        orderResponse.setCustomerId( order.getCustomerId() );
        orderResponse.setCustomerName( order.getCustomerName() );
        orderResponse.setOrderDate( order.getOrderDate() );
        orderResponse.setStatus( order.getStatus() );
        orderResponse.setTotalAmount( order.getTotalAmount() );

        return orderResponse;
    }
}
