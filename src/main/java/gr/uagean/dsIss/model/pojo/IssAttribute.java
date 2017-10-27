/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.model.pojo;

/**
 *
 * @author nikos
 */
public class IssAttribute {

    private String value;
    private int complex;
    private int required;

    public IssAttribute() {
    }

    public IssAttribute(String value, int complex, int required) {
        this.value = value;
        this.complex = complex;
        this.required = required;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getComplex() {
        return complex;
    }

    public void setComplex(int complex) {
        this.complex = complex;
    }

    public int getRequired() {
        return required;
    }

    public void setRequired(int required) {
        this.required = required;
    }

}
