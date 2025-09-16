package employee_self_service.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaginationPayload {
    private boolean paginate;
    private int page;
    private int size;
    private String category;
    private String product;
}
