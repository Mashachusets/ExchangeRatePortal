package com.example.demo.business.exceptions;

public class Exceptions {

    public static class ExchangeRateNotFoundException extends RuntimeException {
        public ExchangeRateNotFoundException(String currencyCode) {
            super(String.format("Exchange rate for currency %s was not found.", currencyCode));
        }
    }

    public static class ExchangeRateJobException extends RuntimeException {
        public ExchangeRateJobException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class DailyExchangeRatesNotFoundException  extends RuntimeException {
        public DailyExchangeRatesNotFoundException (String message) {
            super(message);
        }
    }

    public static class HolidayCalendarLoadException extends RuntimeException {
        public HolidayCalendarLoadException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static class MissingFileException extends RuntimeException {
        public MissingFileException(String message) {
            super(message);
        }
    }
}
