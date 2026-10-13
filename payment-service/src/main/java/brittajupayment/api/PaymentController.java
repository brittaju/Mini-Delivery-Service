package brittajupayment.api;

import brittajucommon.http.payment.PaymentRequestDto;
import brittajucommon.http.payment.PaymentResponseDto;
import brittajupayment.database.PaymentEntity;
import brittajupayment.database.PaymentEntityMapper;
import brittajupayment.domain.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService service;
    private final PaymentEntityMapper mapper;

    @PostMapping
    public PaymentResponseDto createPayment(
            @RequestBody PaymentRequestDto request
    ) {
        PaymentEntity entity = service.createPayment(request);
        return mapper.toPaymentDto(entity);
    }
}
