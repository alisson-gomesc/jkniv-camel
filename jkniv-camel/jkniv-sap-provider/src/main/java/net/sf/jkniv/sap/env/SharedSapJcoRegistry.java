package net.sf.jkniv.sap.env;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.sap.conn.jco.ext.DataProviderException;
import com.sap.conn.jco.ext.DestinationDataProvider;
import com.sap.conn.jco.ext.DataProviderException.Reason;

import net.sf.jkniv.sap.env.StatusDestinationProvider.Status;

/**
 * 
 * @author Alisson Gomes
 * @since 2.20.0
 */
public class SharedSapJcoRegistry
{
    private static final Logger                                 LOG      = Logger.getLogger("SharedSapJcoRegistry");
    private static final Map<String, StatusDestinationProvider> PROVIDES = new HashMap<>();
    
    /**
     * Register a {@code DestinationDataProvider}
     * @param myProvider a logical address of an ABAP system 
     * @param appName Name of application that is registered
     */
    public static void register(final DestinationDataProvider myProvider, final String appName)
    {
        //register the provider with the JCo environment;
        //catch IllegalStateException if an instance is already registered
        try
        {
            if (!com.sap.conn.jco.ext.Environment.isDestinationDataProviderRegistered())
            {
                com.sap.conn.jco.ext.Environment.registerDestinationDataProvider(myProvider);
                PROVIDES.put(appName, new StatusDestinationProvider(Status.REGISTERED, myProvider));
                LOG.info("Destination Data Provider was register [" + myProvider + "] successfully.");
            }
            else
            {
                PROVIDES.put(appName, new StatusDestinationProvider(Status.UNREGISTERED, myProvider));
                LOG.info("Jco Destination global Environment data provider alright registered");

            }
        }
        catch (IllegalStateException providerAlreadyRegisteredException)
        {
            //somebody else registered its implementation, stop the execution
            throw new DataProviderException(Reason.INTERNAL_ERROR, providerAlreadyRegisteredException);
        }
        //set properties for the destination and ...
    }
    
    /**
     * Register a {@code DestinationDataProvider}
     * @param myProvider a logical address of an ABAP system 
     * @param appName Name of application that is registered
     */
    public static void unregister(final DestinationDataProvider myProvider, final String appName)
    {
        try
        {
            if (com.sap.conn.jco.ext.Environment.isDestinationDataProviderRegistered() && PROVIDES.size() == 1)
            {
                com.sap.conn.jco.ext.Environment.unregisterDestinationDataProvider(myProvider);
                LOG.info("Destination Data Provider was unregistered [" + myProvider + "] successfully with app ["+appName+"]");
            }
            else
                LOG.info("App ["+appName+"] was stopped without unregister DestinationDataProvider left ["+getAppNames()+" ] applications running");
        }
        catch (IllegalStateException providerAlreadyRegisteredException)
        {
            LOG.warning("Cannot unregister the DestinationDataProvider for application ["+appName+"]: "
                    + providerAlreadyRegisteredException.getMessage());
            //somebody else registered its implementation, stop the execution
        }
        finally
        {
            PROVIDES.remove(appName);
        }
    }
    
    public static boolean hasRegister(String appName)
    {
        return PROVIDES.containsKey(appName);
    }
    
    private static String getAppNames() {
        StringBuilder sb = new StringBuilder();
        for(String appName: PROVIDES.keySet())
            sb.append(" " + appName);
        return sb.toString();
    }
}
