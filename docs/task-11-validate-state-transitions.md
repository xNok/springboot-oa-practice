# Task 11: Validate State Transitions

## Objective
Implement comprehensive validation logic for order state transitions.

## Requirements
Enhance the state transition validation to cover all business rules and edge cases.

## Business Rules

### Valid State Transitions
| From | To | Description |
|------|-------|-------------|
| CREATED | CONFIRMED | Order payment confirmed |
| CREATED | CANCELLED | Order cancelled before confirmation |
| CONFIRMED | SHIPPED | Order dispatched for delivery |
| CONFIRMED | CANCELLED | Order cancelled after confirmation |
| SHIPPED | DELIVERED | Order successfully delivered |
| SHIPPED | CANCELLED | Order lost or returned during shipping |

### Invalid State Transitions
1. **DELIVERED is final**: Cannot transition from DELIVERED to any other state
2. **No backward transitions**: Cannot go from later to earlier stages (e.g., SHIPPED → CONFIRMED)
3. **Cannot create**: Cannot transition to CREATED from any state
4. **Cannot skip stages**: Generally, should not skip intermediate states (enforced by valid transitions)

### Specific Validation Rules
1. If current status is DELIVERED, reject all transitions
2. If target status is CREATED, reject (cannot reset to initial state)
3. If trying to move backward in the normal flow, reject
4. Only allow transitions explicitly defined in the valid transitions table

## Acceptance Criteria
1. Implement a `isValidTransition(OrderStatus from, OrderStatus to)` helper method
2. Return true only for valid transitions
3. Throw BusinessRuleException with clear message for invalid transitions
4. Error message should indicate both the from and to states
5. Cover all edge cases in validation logic

## Implementation Steps
1. Create a private helper method in OrderService:
   ```java
   private boolean isValidTransition(OrderStatus from, OrderStatus to)
   ```
2. Implement the validation logic using switch/case or a Map
3. Return true for valid transitions, false otherwise
4. In `updateOrderStatus()`, call this method before updating
5. If validation fails, throw:
   ```java
   throw new BusinessRuleException(
     "Invalid state transition from " + from + " to " + to
   );
   ```

## Example Implementation Approach

### Option 1: Switch Statement
```java
private boolean isValidTransition(OrderStatus from, OrderStatus to) {
    // DELIVERED is final
    if (from == OrderStatus.DELIVERED) {
        return false;
    }
    
    // Cannot transition to CREATED
    if (to == OrderStatus.CREATED) {
        return false;
    }
    
    switch (from) {
        case CREATED:
            return to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
        case CONFIRMED:
            return to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED;
        case SHIPPED:
            return to == OrderStatus.DELIVERED || to == OrderStatus.CANCELLED;
        case CANCELLED:
            return false; // Cancelled is also final
        default:
            return false;
    }
}
```

### Option 2: Map of Valid Transitions
```java
private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS = Map.of(
    OrderStatus.CREATED, Set.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED),
    OrderStatus.CONFIRMED, Set.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED),
    OrderStatus.SHIPPED, Set.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED),
    OrderStatus.DELIVERED, Set.of(),
    OrderStatus.CANCELLED, Set.of()
);

private boolean isValidTransition(OrderStatus from, OrderStatus to) {
    return VALID_TRANSITIONS.getOrDefault(from, Set.of()).contains(to);
}
```

## Test Cases to Validate
1. ✅ CREATED → CONFIRMED (valid)
2. ✅ CONFIRMED → SHIPPED (valid)
3. ✅ SHIPPED → DELIVERED (valid)
4. ✅ CREATED → CANCELLED (valid)
5. ✅ CONFIRMED → CANCELLED (valid)
6. ✅ SHIPPED → CANCELLED (valid)
7. ❌ DELIVERED → CANCELLED (invalid - final state)
8. ❌ SHIPPED → CONFIRMED (invalid - backward)
9. ❌ CONFIRMED → CREATED (invalid - cannot reset)
10. ❌ CANCELLED → CONFIRMED (invalid - cancelled is final)

## Expected Time
Included in Task 10 implementation (validation is part of updateOrderStatus)

## Testing
Run: `mvn test -Dtest=Task11ValidateStateTransitionsTest`
