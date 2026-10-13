package brittajucommon.http.order;

import java.math.BigDecimal;
import java.util.Set;

public record OrderResponseDto(
        Long id,
        Long customerId,
        String address,
        BigDecimal totalAmount,
        String courierName,
        Integer etaMinutes,
        OrderStatus orderStatus,
        Set<OrderItemResponseDto> orderItemEntities
) {}