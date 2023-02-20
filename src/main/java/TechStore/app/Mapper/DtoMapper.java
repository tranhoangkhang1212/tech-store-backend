package TechStore.app.Mapper;

import java.util.List;

public interface DtoMapper<E, D> {
    D toDto(E entity);
    List<D> toListDto(List<E> entities);
}
