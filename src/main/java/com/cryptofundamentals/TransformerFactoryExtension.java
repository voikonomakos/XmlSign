package com.cryptofundamentals;

import javax.xml.XMLConstants;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;


/**
 * Creates a preconfigured {@link TransformerFactory}.
 */
public final class TransformerFactoryExtension
{
    private TransformerFactoryExtension()
    {
    }

    /**
     * @return a preconfigured {@link TransformerFactory} that accesses no external DTD and schema for validation.
     */
    public static TransformerFactory newInstance() throws TransformerConfigurationException
    {
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transformerFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");

        return transformerFactory;
    }
}