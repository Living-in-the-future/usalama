package com.gwinto.uzalama;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.QuerySnapshot;

public class ContactItem {
    private String fname;
    private String onames;
    private String email;
    private String phone;

    public ContactItem() { }

    public ContactItem(String fname,String onames, String email, String phone)
    {
        this.fname = fname;
        this.onames = onames;
        this.email = email;
        this.phone = phone;
    }

    public void setFname(String fname)
    {
        this.fname = fname;
    }

    public String getFname()
    {
        return fname;
    }

    public void setOnames(String onames)
    {
        this.onames = onames;
    }

    public String getOnames()
    {
        return onames;
    }

    public void setEmail(String em)
    {
        this.email = em;
    }

    public String getEmail()
    {
        return  email;
    }

    public void setPhone(String ph)
    {
        this.phone = ph;
    }

    public String getPhone()
    {
        return phone;
    }

}
