package brittajuorder.api;

import brittajucommon.http.order.OrderRequestDto;
import brittajucommon.http.order.OrderResponseDto;
import brittajuorder.database.OrderEntity;
import brittajuorder.database.OrderEntityMapper;
import brittajuorder.domain.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final OrderEntityMapper mapper;

    @PostMapping
    public OrderResponseDto create(
            @RequestBody OrderRequestDto request
    ) {
        OrderEntity entity = orderService.create(request);
        return mapper.toOrderDto(entity);
    }

    @GetMapping("/{id}")
    public OrderResponseDto getOne(
            @PathVariable Long id
    ) {
        OrderEntity entity = orderService.getOne(id);
        return mapper.toOrderDto(entity);
    }

    @PostMapping("/{id}/pay")
    public OrderResponseDto payOrder(
            @PathVariable Long id,
            @RequestBody OrderPaymentRequest request
    ) {
        OrderEntity entity = orderService.processPayment(id, request);
        return mapper.toOrderDto(entity);
    }
}
