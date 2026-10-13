package brittajuorder.external;

import brittajucommon.http.payment.PaymentRequestDto;
import brittajucommon.http.payment.PaymentResponseDto;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

@HttpExchange(
        accept = "application/json",
        contentType = "application/json",
        url = "/api/payments"
)
public interface PaymentHttpClient {

    @PostExchange
    PaymentResponseDto createPayment(
            @RequestBody PaymentRequestDto request);
}
