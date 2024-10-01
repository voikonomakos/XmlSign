package com.cryptofundamentals;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;


/**
 * Builds a preconfigured {@link DocumentBuilderFactory}.
 */
public final class DocumentBuilderFactoryNoExternalEntities
{
    private DocumentBuilderFactoryNoExternalEntities()
    {
    }

    /**
     * @return a preconfigured {@link DocumentBuilderFactory} that accesses no external DTD and schema for validation.
     */
    public static DocumentBuilderFactory newInstance() throws ParserConfigurationException
    {
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        dbf.setAttribute(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        return dbf;
    }
}
