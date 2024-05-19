package ru.practicum.booking.model;

import lombok.*;
import ru.practicum.item.model.Item;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@Data
@Builder
@ToString
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "booking_id")
    private Long id;

    @Column(name = "start_booking")
    private LocalDateTime start;

    @Column(name = "finish_booking")
    private LocalDateTime finish;

    @ManyToOne
    @JoinColumn(name = "items_id")
    private Item item;

    @ManyToOne
    @JoinColumn(name = "users_id")
    private User booker;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private Status status;
}