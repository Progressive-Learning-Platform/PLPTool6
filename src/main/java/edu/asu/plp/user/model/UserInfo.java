package edu.asu.plp.user.model;

public class UserInfo {
    private String name;
    private String email;
    private String org_school;
    private String gender;
    private String dateOfBirth;
    private String contact_no;
    private String alt_no;
    private String profile_photo;

    public boolean isProfileComplete() {
        return isProfileComplete;
    }

    public void checkProfileComplete() {
        if(name.isEmpty() || email.isEmpty() || contact_no.isEmpty() || org_school.isEmpty()){
            isProfileComplete = false;
        }else{
            isProfileComplete = true;
        }
    }

    private boolean isProfileComplete;

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOrg_school() {
        return org_school;
    }

    public void setOrg_school(String org_school) {
        this.org_school = org_school;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContact_no() {
        return contact_no;
    }

    public void setContact_no(String contact_no) {
        this.contact_no = contact_no;
    }

    public String getAlt_no() {
        return alt_no;
    }

    public void setAlt_no(String alt_no) {
        this.alt_no = alt_no;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }
}
