package com.example.oa.mapper;

import com.example.oa.dto.OrderResponse;
import com.example.oa.entity.Order;
import org.mapstruct.Mapper;

/**
 * MapStruct mapper for Order entity to OrderResponse DTO conversion.
 * 
 * OPTIONAL: This is an example of how to use MapStruct for automated mapping.
 * Candidates are NOT required to use this - manual mapping is equally valid.
 * 
 * For Order->OrderResponse mapping, all fields align perfectly, so no
 * special @Mapping annotations are needed. MapStruct will automatically
 * map fields with matching names.
 * 
 * Usage in Service:
 * @Autowired
 * private OrderMapper orderMapper;
 * 
 * public OrderResponse getOrderById(Long id) {
 *     Order order = orderRepository.findById(id).orElse(null);
 *     return orderMapper.toResponse(order);
 * }
 */
@Mapper(componentModel = "spring")
public interface OrderMapper {

    /**
     * Maps an Order entity to OrderResponse DTO.
     * All fields match automatically - no custom mapping needed!
     * 
     * @param order the order entity to map (can be null)
     * @return the mapped response DTO, or null if input is null
     */
    OrderResponse toResponse(Order order);
    
    /**
     * Safely maps an Order entity to OrderResponse DTO with null check.
     * 
     * This is a default method that adds a null safety check before mapping.
     * Use this method when you're unsure if the entity might be null.
     * 
     * @param order the order entity to map (can be null)
     * @return the mapped response DTO, or null if input is null
     */
    default OrderResponse toResponseSafe(Order order) {
        if (order == null) {
            return null;
        }
        return toResponse(order);
    }
}
