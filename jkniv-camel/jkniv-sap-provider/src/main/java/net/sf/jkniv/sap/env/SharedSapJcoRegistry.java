package net.sf.jkniv.sap.env;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

import com.sap.conn.jco.ext.DestinationDataProvider;

import net.sf.jkniv.sap.env.StatusDestinationProvider.Status;

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
            //somebody else registered its implementation, 
            //stop the execution
            throw new Error(providerAlreadyRegisteredException);
        }
        //set properties for the destination and ...
    }
    
    /**
     * Register a {@code DestinationDataProvider}
     * @param myProvider a logical address of an ABAP system 
     * @param appName Name of application that is registered
     */
    public static void undoRegister(final DestinationDataProvider myProvider, final String appName)
    {
        try
        {
            if (com.sap.conn.jco.ext.Environment.isDestinationDataProviderRegistered())
            {
                com.sap.conn.jco.ext.Environment.unregisterDestinationDataProvider(myProvider);
                LOG.info("Destination Data Provider was unregistered [" + myProvider + "] successfully.");
            }
        }
        catch (IllegalStateException providerAlreadyRegisteredException)
        {
            LOG.warning("Cannot deregister the DestinationDataProvider: "
                    + providerAlreadyRegisteredException.getMessage());
            //somebody else registered its implementation, stop the execution
        }
        finally
        {
            StatusDestinationProvider status = PROVIDES.get(appName);
            if (status != null && status.getStatus() == Status.REGISTERED)
            {
                PROVIDES.remove(appName);
                if (!PROVIDES.isEmpty())
                {
                    Entry<String, StatusDestinationProvider> entry = PROVIDES.entrySet().iterator().next();
                    StatusDestinationProvider newRegister = entry.getValue();
                    register(newRegister.getDestinationDataProvider(), entry.getKey());
                    LOG.info("New Destination was registered [" + newRegister.getDestinationDataProvider()
                            + "] for application [" + entry.getKey() + "]");
                }
            }
            else
                PROVIDES.remove(appName);
        }
    }
    
    public static boolean hasRegister(String appName)
    {
        return PROVIDES.containsKey(appName);
    }
}
