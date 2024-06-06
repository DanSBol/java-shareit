package ru.practicum.shareit.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(value = "select re from Request as re join re.requestor as r where r.id = ?1 order by re.id")
    List<RequestDto> getRequestsByOwner(long userId);

    @Query(value = "select re from Request as re join re.requestor as r where r.id != ?1 order by re.id")
    Page<RequestDto> getRequestsByParam(long userId, Pageable pageable);
}