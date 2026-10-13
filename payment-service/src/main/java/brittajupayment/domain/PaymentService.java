package brittajupayment.domain;

import brittajucommon.http.payment.PaymentMethod;
import brittajucommon.http.payment.PaymentRequestDto;
import brittajucommon.http.payment.PaymentStatus;
import brittajupayment.database.PaymentEntity;
import brittajupayment.database.PaymentEntityMapper;
import brittajupayment.database.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentEntityMapper mapper;

    public PaymentEntity createPayment(
            PaymentRequestDto request
    ) {
        Optional<PaymentEntity> foundEntity = repository.findByOrderId(request.orderId());
        if (foundEntity.isPresent()) {
            return foundEntity.get();
        }
        PaymentEntity entity = mapper.toEntity(request);
        PaymentStatus status = request.paymentMethod().equals(PaymentMethod.QR)
                ? PaymentStatus.PAYMENT_FAILED
                : PaymentStatus.PAYMENT_SUCCEEDED;
        entity.setPaymentStatus(status);
        repository.save(entity);
        return entity;
    }
}
