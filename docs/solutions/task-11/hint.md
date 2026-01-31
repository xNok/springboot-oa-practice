# Task 11 Solution Hint: Validate State Transitions

## Overview
This task is integrated into Task 10. The validation logic ensures business rules are enforced.

## Complete Validation Implementation

### Approach 1: Switch Statement
```java
private boolean isValidTransition(OrderStatus from, OrderStatus to) {
    // Rule 1: DELIVERED is a final state
    if (from == OrderStatus.DELIVERED) {
        return false;
    }
    
    // Rule 2: CANCELLED is a final state
    if (from == OrderStatus.CANCELLED) {
        return false;
    }
    
    // Rule 3: Cannot transition to CREATED (initial state only)
    if (to == OrderStatus.CREATED) {
        return false;
    }
    
    // Rule 4: Define valid forward transitions
    switch (from) {
        case CREATED:
            return to == OrderStatus.CONFIRMED || to == OrderStatus.CANCELLED;
        case CONFIRMED:
            return to == OrderStatus.SHIPPED || to == OrderStatus.CANCELLED;
        case SHIPPED:
            return to == OrderStatus.DELIVERED || to == OrderStatus.CANCELLED;
        default:
            return false;
    }
}
```

### Approach 2: Map-Based (Cleaner)
```java
private static final Map<OrderStatus, Set<OrderStatus>> VALID_TRANSITIONS;

static {
    Map<OrderStatus, Set<OrderStatus>> transitions = new EnumMap<>(OrderStatus.class);
    transitions.put(OrderStatus.CREATED, 
        EnumSet.of(OrderStatus.CONFIRMED, OrderStatus.CANCELLED));
    transitions.put(OrderStatus.CONFIRMED, 
        EnumSet.of(OrderStatus.SHIPPED, OrderStatus.CANCELLED));
    transitions.put(OrderStatus.SHIPPED, 
        EnumSet.of(OrderStatus.DELIVERED, OrderStatus.CANCELLED));
    transitions.put(OrderStatus.DELIVERED, EnumSet.noneOf(OrderStatus.class));
    transitions.put(OrderStatus.CANCELLED, EnumSet.noneOf(OrderStatus.class));
    VALID_TRANSITIONS = Collections.unmodifiableMap(transitions);
}

private boolean isValidTransition(OrderStatus from, OrderStatus to) {
    return VALID_TRANSITIONS.getOrDefault(from, EnumSet.noneOf(OrderStatus.class))
                            .contains(to);
}
```

### Approach 3: Enum-Based State Machine (Best for Production)
```java
public enum OrderStatus {
    CREATED {
        @Override
        public boolean canTransitionTo(OrderStatus target) {
            return target == CONFIRMED || target == CANCELLED;
        }
    },
    CONFIRMED {
        @Override
        public boolean canTransitionTo(OrderStatus target) {
            return target == SHIPPED || target == CANCELLED;
        }
    },
    SHIPPED {
        @Override
        public boolean canTransitionTo(OrderStatus target) {
            return target == DELIVERED || target == CANCELLED;
        }
    },
    DELIVERED {
        @Override
        public boolean canTransitionTo(OrderStatus target) {
            return false; // Final state
        }
    },
    CANCELLED {
        @Override
        public boolean canTransitionTo(OrderStatus target) {
            return false; // Final state
        }
    };
    
    public abstract boolean canTransitionTo(OrderStatus target);
}

// Usage in service:
private boolean isValidTransition(OrderStatus from, OrderStatus to) {
    return from.canTransitionTo(to);
}
```

## State Transition Matrix (Reference)

```
         → CREATED  CONFIRMED  SHIPPED  DELIVERED  CANCELLED
CREATED       X         ✓         X         X          ✓
CONFIRMED     X         X         ✓         X          ✓
SHIPPED       X         X         X         ✓          ✓
DELIVERED     X         X         X         X          X
CANCELLED     X         X         X         X          X
```

## Test Cases

```java
// Valid transitions
isValidTransition(CREATED, CONFIRMED)    // true
isValidTransition(CREATED, CANCELLED)    // true
isValidTransition(CONFIRMED, SHIPPED)    // true
isValidTransition(CONFIRMED, CANCELLED)  // true
isValidTransition(SHIPPED, DELIVERED)    // true
isValidTransition(SHIPPED, CANCELLED)    // true

// Invalid transitions
isValidTransition(DELIVERED, CANCELLED)  // false - DELIVERED is final
isValidTransition(CANCELLED, CONFIRMED)  // false - CANCELLED is final
isValidTransition(SHIPPED, CONFIRMED)    // false - backward transition
isValidTransition(CONFIRMED, CREATED)    // false - cannot reset
isValidTransition(CREATED, SHIPPED)      // false - skips CONFIRMED
```

## Error Messages

Provide descriptive error messages:
```java
if (!isValidTransition(currentStatus, newStatus)) {
    String message = String.format(
        "Invalid state transition from %s to %s", 
        currentStatus, 
        newStatus
    );
    
    // Optional: Add reason
    if (currentStatus == OrderStatus.DELIVERED) {
        message += ". DELIVERED orders cannot be modified.";
    } else if (currentStatus == OrderStatus.CANCELLED) {
        message += ". CANCELLED orders cannot be modified.";
    }
    
    throw new BusinessRuleException(message);
}
```

## Key Principles

1. **Final States**: DELIVERED and CANCELLED are terminal
2. **No Backward Transitions**: Cannot move from later to earlier stages
3. **No Reset**: Cannot transition back to CREATED
4. **Sequential Flow**: Normal flow is CREATED → CONFIRMED → SHIPPED → DELIVERED
5. **Cancellation**: Can cancel from CREATED, CONFIRMED, or SHIPPED (but not DELIVERED)
