package es.batbatcar.v2p4.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Validator {

    public static boolean isValidName(String param) {
        return isNotEmptyOrNull(param) && param.length() >= 5 && Character.isUpperCase(param.charAt(0));
    }

    private static boolean isNotEmptyOrNull(String param) {
        return param != null && !param.isEmpty();
    }

    // Incluye aquí el resto de métodos de validación que necesites

    public static boolean isValidRoute(String route) {
        return isNotEmptyOrNull(route) && route.matches("^[a-zA-Z]+\\s?-\\s?[a-zA-Z]+$");
    }

    public static boolean isValidPropietario(String propietario) {
        return isNotEmptyOrNull(propietario) && propietario.matches("^[A-Z][a-zA-Z]\\s[A-Z][a-zA-Z]$");
    }

    public static boolean isPositiveInt(int number) {
        return number > 0;
    }

    public static boolean isPositiveFloat(float number) {
        return number > 0;
    }

    public static boolean isValidDateTime(String dateTime) {
        try {
            LocalDate.parse(dateTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }
    public static boolean isValidDate(String date) {
        try {
            LocalDate.parse(date, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidTime(String time) {
        try {
            LocalTime.parse(time, DateTimeFormatter.ofPattern("HH:mm"));
        } catch (DateTimeParseException e) {
            return false;
        }
        return true;
    }

    public static boolean isValidPlazasOfertadas(int plazas) {
        return plazas >= 1 && plazas <= 6;
    }

}
