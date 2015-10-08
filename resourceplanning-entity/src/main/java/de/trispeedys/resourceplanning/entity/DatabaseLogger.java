package de.trispeedys.resourceplanning.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import de.trispeedys.resourceplanning.entity.misc.DbLogLevel;
import de.trispeedys.resourceplanning.entity.misc.HelperState;

@Entity
@Table(name = "database_logger")
public class DatabaseLogger extends AbstractDbObject
{
    private Date date;
    
    private String message;
    
    @Column(name = "business_key")
    private String businessKey;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "log_level")
    @NotNull
    private DbLogLevel logLevel;

    public Date getDate()
    {
        return date;
    }

    public void setDate(Date date)
    {
        this.date = date;
    }

    public String getMessage()
    {
        return message;
    }
    
    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getBusinessKey()
    {
        return businessKey;
    }

    public void setBusinessKey(String businessKey)
    {
        this.businessKey = businessKey;
    }
    
    public DbLogLevel getLogLevel()
    {
        return logLevel;
    }
    
    public void setLogLevel(DbLogLevel logLevel)
    {
        this.logLevel = logLevel;
    }
}