package ru.practicum.shareit.exceptions;

public class ValidationBookingStatusException extends RuntimeException {
    public ValidationBookingStatusException(String message) {
        super(message);
    }
}
