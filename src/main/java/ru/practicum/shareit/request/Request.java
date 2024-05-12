package ru.practicum.shareit.request;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.Item;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests")
@Data
@Builder(builderClassName = "RequestBuilder")
@EqualsAndHashCode
public class Request implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requestor_id", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private User requestor;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item", insertable = false, updatable = false)
    @Fetch(FetchMode.JOIN)
    private Item item;
    private String description;
    private LocalDateTime created;

    public static class RequestBuilder {
        public RequestBuilder() {
            // Пустой конструктор
        }
    }
}