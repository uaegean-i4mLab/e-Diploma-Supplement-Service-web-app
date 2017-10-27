/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.uagean.dsIss.model.pojo;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author nikos
 */
public class IssAttributeList {

    private String status;
    private Map<String, IssAttribute> list;

    public IssAttributeList() {
        this.status = "OK";
        this.list = new HashMap();
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Map<String, IssAttribute> getList() {
        return list;
    }

    public void setList(Map<String, IssAttribute> list) {
        this.list = list;
    }

}
