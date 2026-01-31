package com.example.oa.mapper;

import com.example.oa.dto.CartItemResponse;
import com.example.oa.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * MapStruct mapper for CartItem entity to CartItemResponse DTO conversion.
 * 
 * OPTIONAL: This is an example of how to use MapStruct for automated mapping.
 * Candidates are NOT required to use this - manual mapping is equally valid.
 * 
 * Benefits of MapStruct:
 * - Generates mapping code at compile-time (type-safe)
 * - Reduces boilerplate compared to manual mapping
 * - Better performance than reflection-based mappers
 * 
 * Usage in Service:
 * @Autowired
 * private CartItemMapper cartItemMapper;
 * 
 * public List<CartItemResponse> getAllCartItems() {
 *     return cartItemRepository.findAll().stream()
 *         .map(cartItemMapper::toResponse)
 *         .collect(Collectors.toList());
 * }
 */
@Mapper(componentModel = "spring")
public interface CartItemMapper {

    /**
     * Maps a CartItem entity to CartItemResponse DTO.
     * 
     * The @Mapping annotation handles special cases:
     * - ignore: Fields that shouldn't be mapped
     * - expression: Custom Java expressions for calculated fields
     * 
     * @param cartItem the cart item entity to map (can be null)
     * @return the mapped response DTO, or null if input is null
     */
    @Mapping(target = "subtotal", expression = "java(cartItem.getQuantity() * cartItem.getPrice())")
    CartItemResponse toResponse(CartItem cartItem);
    
    /**
     * Safely maps a CartItem entity to CartItemResponse DTO with null check.
     * 
     * This is a default method that adds a null safety check before mapping.
     * Use this method when you're unsure if the entity might be null.
     * 
     * @param cartItem the cart item entity to map (can be null)
     * @return the mapped response DTO, or null if input is null
     */
    default CartItemResponse toResponseSafe(CartItem cartItem) {
        if (cartItem == null) {
            return null;
        }
        return toResponse(cartItem);
    }
}
