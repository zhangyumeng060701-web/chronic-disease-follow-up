package com.example.followup.util;

import com.example.followup.entity.Patient;
import org.springframework.beans.BeanUtils;

public final class PatientDesensitizationUtil {

    private PatientDesensitizationUtil() {
    }

    public static Patient maskForRole(Patient patient, String role) {
        if (patient == null || isAdmin(role)) {
            return patient;
        }

        Patient masked = new Patient();
        BeanUtils.copyProperties(patient, masked);
        masked.setName(maskName(patient.getName()));
        masked.setPhone(maskPhone(patient.getPhone()));
        masked.setIdCard(maskIdCard(patient.getIdCard()));
        masked.setAddress(maskAddress(patient.getAddress()));
        return masked;
    }

    public static boolean isAdmin(String role) {
        return "ADMIN".equalsIgnoreCase(role);
    }

    public static String maskName(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        if (value.length() == 1) {
            return value;
        }
        return value.charAt(0) + repeat("*", value.length() - 1);
    }

    public static String maskPhone(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.replaceAll("^(\\d{3})\\d{4}(\\d{4})$", "$1****$2");
    }

    public static String maskIdCard(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        return value.replaceAll("^(.{6}).{8}(.{4})$", "$1********$2");
    }

    public static String maskAddress(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        String district = value.replaceAll("^(.+?[\\u533a\\u53bf]).*$", "$1");
        return district.equals(value) ? "****" : district + "****";
    }

    private static String repeat(String value, int count) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            builder.append(value);
        }
        return builder.toString();
    }
}
