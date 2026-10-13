package brittajuorder.database;

import brittajucommon.http.order.OrderRequestDto;
import brittajucommon.http.order.OrderResponseDto;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderEntityMapper {

    OrderEntity toEntity(OrderRequestDto request);

    OrderResponseDto toOrderDto(OrderEntity orderEntity);
}