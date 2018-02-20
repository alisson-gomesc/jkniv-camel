package net.sf.jkniv.camel.sap.jco3;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.conn.jco.ext.DestinationDataProvider;

public class SapDataProviderFactory
{
    private static final Logger LOG = LoggerFactory.getLogger(SapDataProviderFactory.class);
    public static final String SHARED_DATA_PROVIDER = "net.sf.jkniv.sap.env.SharedDestinationDataProvider";
    
    private static DestinationDataProvider sapDataProvider;
    
    public static DestinationDataProvider getInstance()
    {
        if (sapDataProvider == null)
        {
            setInstance();
        }
        if (sapDataProvider == null)
            sapDataProvider = new SapJcoDestinationDataProvider();
            
        LOG.debug("DestinationDataProvider instanceof {}", (sapDataProvider != null ? sapDataProvider.getClass().getName() : "null"));
        return sapDataProvider;
    }
    
    private static void setInstance()
    {
        try
        {
            Class clazz = Class.forName(SHARED_DATA_PROVIDER);
            Method method = clazz.getDeclaredMethod("getInstance");
            sapDataProvider = (DestinationDataProvider) method.invoke(null);
        }
        catch (Exception e)
        {
            LOG.error("Cannot invoke "+SHARED_DATA_PROVIDER+".getInstance()", e);
        }
    }
}
