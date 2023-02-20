package TechStore.app.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PageResponseDto<T> extends PageDto {
    private List<T> listItem;
}
