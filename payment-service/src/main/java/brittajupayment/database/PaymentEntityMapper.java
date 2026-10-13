package brittajupayment.database;

import brittajucommon.http.payment.PaymentRequestDto;
import brittajucommon.http.payment.PaymentResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentEntityMapper {

    PaymentEntity toEntity(PaymentRequestDto request);

    PaymentResponseDto toPaymentDto(PaymentEntity paymentEntity);
}