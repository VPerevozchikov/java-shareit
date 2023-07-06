package ru.practicum.shareit.exceptions;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ExceptionsTest {
    private ErrorHandler errorHandler = new ErrorHandler();

    @Test
    void handleNotFoundExceptionTest() {
        NotFoundException ex = new NotFoundException("message");
        ErrorResponse errorResponse = errorHandler.handleNotFoundException(ex);
        String exceptionMessage = errorResponse.getError();
        Assertions.assertEquals(exceptionMessage, "message");
    }

    @Test
    void handleValidationExceptionTest() {
        ValidationException ex = new ValidationException("Ошибка в запросе.");
        ErrorResponse errorResponse = errorHandler.handleValidationException(ex);
        String exceptionMessage = errorResponse.getError();
        Assertions.assertEquals(exceptionMessage, "Ошибка в запросе.");
    }

    @Test
    void handleValidationBookingStatusExceptionTest() {
        ValidationBookingStatusException ex = new ValidationBookingStatusException("Unknown state: UNSUPPORTED_STATUS");
        ErrorResponse errorResponse = errorHandler.handleValidationBookingStatusException(ex);
        String exceptionMessage = errorResponse.getError();
        Assertions.assertEquals(exceptionMessage, "Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void handleEmailDuplicateExceptionTest() {
        EmailDuplicateException ex = new EmailDuplicateException("Email уже существует");
        ErrorResponse errorResponse = errorHandler.handleEmailDuplicateException(ex);
        String exceptionMessage = errorResponse.getError();
        Assertions.assertEquals(exceptionMessage, "Email уже существует");
    }
}
