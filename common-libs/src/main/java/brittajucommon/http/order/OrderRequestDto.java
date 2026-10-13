package brittajucommon.http.order;

import java.util.Set;

public record OrderRequestDto(
        Long customerId,
        String address,
        Set<OrderItemRequestDto> items
) {}
