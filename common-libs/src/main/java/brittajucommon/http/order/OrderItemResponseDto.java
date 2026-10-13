package brittajucommon.http.order;

import java.math.BigDecimal;

public record OrderItemResponseDto(
        Long id,
        Long itemId,
        Integer quantity,
        BigDecimal priceAtPurchase
) {}