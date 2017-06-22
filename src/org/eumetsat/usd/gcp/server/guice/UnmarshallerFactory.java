//@formatter:off
/*
 * PROJECT: gcp
 * AUTHOR: USC/C/PBe
 * COPYRIGHT: EUMETSAT 2015
 */
//@formatter:on
package org.eumetsat.usd.gcp.server.guice;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author USC/C/PBe
 * 
 */
public class UnmarshallerFactory
{
    /** Logger for this class. */
    private static final Logger LOGGER = LoggerFactory.getLogger(UnmarshallerFactory.class);

    /**
     * Create a <code>Unmarshaller</code> object for a certain package.
     * 
     * @param contextPath
     *            package name where the JAXB pojos are stored.
     * @return an <code>Unmarshaller</code> object for the input package.
     */
    public Unmarshaller create(final String contextPath)
    {
        Unmarshaller unmarshaller = null;
        try
        {
            JAXBContext jc = JAXBContext.newInstance(contextPath);
            unmarshaller = jc.createUnmarshaller();

        } catch (JAXBException je)
        {
            LOGGER.error("Unexpected error creating the JAXB unmarshaller. Aborting execution.", je);
            throw new RuntimeException("Unexpected error creating the JAXB unmarshaller. Aborting execution.", je);
        }

        return unmarshaller;
    }
}
