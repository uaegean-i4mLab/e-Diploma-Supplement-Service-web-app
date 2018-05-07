/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.model.pojo;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;

/**
 *
 * @author nikos
 */
public class IssErrorResponse {

    private IssAttribute statusCode;
    private IssAttribute statusMessage;

    public IssErrorResponse() {

    }

    @JsonGetter("StatusCode")
    public IssAttribute getStatusCode() {
        return statusCode;
    }

    @JsonSetter("StatusCode")
    public void setStatusCode(IssAttribute StatusCode) {
        this.statusCode = StatusCode;
    }

    @JsonGetter("StatusMessage")
    public IssAttribute getStatusMessage() {
        return statusMessage;
    }

    @JsonSetter("StatusMessage")
    public void setStatusMessage(IssAttribute StatusMessage) {
        this.statusMessage = StatusMessage;
    }

    public class IssAttribute {

        private String value;
        private String complex;
        private String required;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getComplex() {
            return complex;
        }

        public void setComplex(String complex) {
            this.complex = complex;
        }

        public String getRequired() {
            return required;
        }

        public void setRequired(String required) {
            this.required = required;
        }

    }

}
