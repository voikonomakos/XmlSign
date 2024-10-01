package com.cryptofundamentals;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class XmlSignerTests {
    private static final String REPORT_FILE_PATH = "C:\\Projects\\Pwc\\Test\\Germany\\Reports\\test_report.xml";
    private static final String CERTIFICATE_FILE_PATH = "C:\\Projects\\Pwc\\Test\\Germany\\certs\\cert.pem";
    private static final String PRIVATE_KEY_FILE_PATH = "C:\\Projects\\Pwc\\Test\\Germany\\certs\\key.pem";
    private static final String OUTPUT_SIGNED_XML_FILE_PATH = "C:\\Projects\\Pwc\\Test\\Germany\\Reports\\signed_file.xml";

    @Test
    public void testSignXml() throws Exception {

        String signedXml = XmlSigner.signXMLDocument(REPORT_FILE_PATH, PRIVATE_KEY_FILE_PATH, CERTIFICATE_FILE_PATH);

        Path path = Paths.get(OUTPUT_SIGNED_XML_FILE_PATH);
        Files.write(path, signedXml.getBytes());

        assertNotNull(signedXml);
    }
}
