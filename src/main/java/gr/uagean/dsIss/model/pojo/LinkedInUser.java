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
public class LinkedInUser {

    private String firstName;
    private String headline;
    private String id;
    private String lastName;
    private String emailAddress; // private String emailAddress;
    private SiteStandardProfileRequest siteStandardProfileRequest;

    public LinkedInUser() {
    }

    public LinkedInUser(String firstName, String headline, String id, String lastName, SiteStandardProfileRequest siteStandardProfileRequest, String email) {
        this.firstName = firstName;
        this.headline = headline;
        this.id = id;
        this.lastName = lastName;
        this.siteStandardProfileRequest = siteStandardProfileRequest;
        this.emailAddress = email;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public SiteStandardProfileRequest getSiteStandardProfileRequest() {
        return siteStandardProfileRequest;
    }

    public void setSiteStandardProfileRequest(SiteStandardProfileRequest siteStandardProfileRequest) {
        this.siteStandardProfileRequest = siteStandardProfileRequest;
    }

    public class SiteStandardProfileRequest {

        private String url;

        public SiteStandardProfileRequest(String url) {
            this.url = url;
        }

        public SiteStandardProfileRequest() {
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

    }

}
