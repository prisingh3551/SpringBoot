package com.priyasingh.ecommerce.exceptions;

public class ResourseNotFoundException extends RuntimeException{
    String resourseName;
    String field;
    String fieldName;
    Long fieldId;

    public ResourseNotFoundException() {
    }

    public ResourseNotFoundException(String resourseName, String field, String fieldName) {
        super(String.format("%s not found with %s: %s", resourseName, field, fieldName));
        this.resourseName = resourseName;
        this.field = field;
        this.fieldName = fieldName;
    }

    public ResourseNotFoundException(String resourseName, String field, Long fieldId) {
        super(String.format("%s not found with %s: %d", resourseName, field, fieldId));
        this.resourseName = resourseName;
        this.field = field;
        this.fieldId = fieldId;
    }
}
