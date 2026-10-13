package brittajucommon.http.payment;

import java.math.BigDecimal;

public record PaymentResponseDto(
        Long paymentId,
        PaymentStatus paymentStatus,
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount
) {}
