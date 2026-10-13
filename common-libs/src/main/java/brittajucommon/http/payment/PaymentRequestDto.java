package brittajucommon.http.payment;

import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record PaymentRequestDto(
        Long orderId,
        PaymentMethod paymentMethod,
        BigDecimal amount
) {}
