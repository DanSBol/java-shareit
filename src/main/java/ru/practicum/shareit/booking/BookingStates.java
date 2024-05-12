package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookingStates {
    ALL("ALL"),
    CURRENT("CURRENT"),
    PAST("**PAST**"),
    FUTURE("FUTURE"),
    WAITING("WAITING"),
    REJECTED("REJECTED");

    private final String realName;
}