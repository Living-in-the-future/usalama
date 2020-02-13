package com.gwinto.uzalama;

public class HistoryItem {
    private String fname;
    private String onames;
    private String email;
    private String phone;
    private String message;
    private String timestamp;

    public HistoryItem() { }

    public HistoryItem(String fname,String onames, String email, String phone, String message, String timestamp)
    {
        this.fname = fname;
        this.onames = onames;
        this.email = email;
        this.phone = phone;
        this.message = message;
        this.timestamp = timestamp;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getMessage()
    {
        return message;
    }

    public void setTimestamp(String timestamp)
    {
        this.timestamp = timestamp;
    }

    public String getTimestamp()
    {
        return timestamp;
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
