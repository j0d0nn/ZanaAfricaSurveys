package org.zanaafrica.surveys.model;

import java.util.Date;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.condition.IfNotNull;

@Entity
public class Participant
{
    @Id
    private Long id;
    
    @Index(IfNotNull.class)
    private String uin;
    
    @Index(IfNotNull.class)
    private String name;
    
    @Index(IfNotNull.class)
    private Date birthday;
    
    @Index(IfNotNull.class)
    private String phoneNumber;    
    
    ///////////////////////////////////////////////////////////////
    
    protected Participant () { }
    
    public Participant (String uin, String name, Date birthday, String phoneNumber) {
        this.uin = uin;
        this.name = name;
        this.birthday = birthday;
        this.phoneNumber = phoneNumber;        
    }
    
    ///////////////////////////////////////////////////////////////

    public Long getId () { return id; }
    public String getUin () { return uin; }
    public String getName () { return name; }
    public Date getBirthday () { return birthday; }
    public String getPhoneNumber () { return phoneNumber; }

    public void setUin (String uin) { this.uin = uin; }
    public void setName (String name) { this.name = name; }
    public void setBirthday (Date birthday) { this.birthday = birthday; }
    public void setPhoneNumber (String phoneNumber) { this.phoneNumber = phoneNumber; }

    ///////////////////////////////////////////////////////////////

    @Override
    public int hashCode ()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((birthday == null) ? 0 : birthday.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + ((phoneNumber == null) ? 0 : phoneNumber.hashCode());
        result = prime * result + ((uin == null) ? 0 : uin.hashCode());
        return result;
    }

    @Override
    public boolean equals (Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Participant other = (Participant) obj;
        if (birthday == null)
        {
            if (other.birthday != null)
                return false;
        }
        else if (!birthday.equals(other.birthday))
            return false;
        if (id == null)
        {
            if (other.id != null)
                return false;
        }
        else if (!id.equals(other.id))
            return false;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        if (phoneNumber == null)
        {
            if (other.phoneNumber != null)
                return false;
        }
        else if (!phoneNumber.equals(other.phoneNumber))
            return false;
        if (uin == null)
        {
            if (other.uin != null)
                return false;
        }
        else if (!uin.equals(other.uin))
            return false;
        return true;
    }
}
