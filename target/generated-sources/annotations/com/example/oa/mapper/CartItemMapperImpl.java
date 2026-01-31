package com.example.oa.mapper;

import com.example.oa.dto.CartItemResponse;
import com.example.oa.entity.CartItem;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2026-01-31T16:01:41+0000",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 25.0.1 (Microsoft)"
)
@Component
public class CartItemMapperImpl implements CartItemMapper {

    @Override
    public CartItemResponse toResponse(CartItem cartItem) {
        if ( cartItem == null ) {
            return null;
        }

        CartItemResponse cartItemResponse = new CartItemResponse();

        cartItemResponse.setId( cartItem.getId() );
        cartItemResponse.setOrderId( cartItem.getOrderId() );
        cartItemResponse.setProductId( cartItem.getProductId() );
        cartItemResponse.setProductName( cartItem.getProductName() );
        cartItemResponse.setQuantity( cartItem.getQuantity() );
        cartItemResponse.setPrice( cartItem.getPrice() );

        cartItemResponse.setSubtotal( cartItem.getQuantity() * cartItem.getPrice() );

        return cartItemResponse;
    }
}
