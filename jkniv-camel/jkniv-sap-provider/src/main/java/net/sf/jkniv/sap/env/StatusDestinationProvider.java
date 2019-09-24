package net.sf.jkniv.sap.env;

import com.sap.conn.jco.ext.DestinationDataProvider;

/**
 * 
 * @author Alisson Gomes
 * @since 2.20.0
 */
public class StatusDestinationProvider
{
    public enum Status {REGISTERED, UNREGISTERED}
    private Status status;
    private DestinationDataProvider destinationDataProvider;
    
    public StatusDestinationProvider(Status status, DestinationDataProvider destinationDataProvider)
    {
        this.status = status;
        this.destinationDataProvider = destinationDataProvider;
    }
    
    public Status getStatus()
    {
        return status;
    }
    
    public DestinationDataProvider getDestinationDataProvider()
    {
        return destinationDataProvider;
    }

    public void asRegister()
    {
        this.status = Status.REGISTERED;
    }
    
    @Override
    public String toString()
    {
        return "StatusDestinationProvider [status=" + status + ", destinationDataProvider=" + destinationDataProvider
                + "]";
    }
}
