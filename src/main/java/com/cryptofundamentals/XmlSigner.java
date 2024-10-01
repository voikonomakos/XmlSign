package com.cryptofundamentals;

import java.nio.charset.StandardCharsets;

import org.bouncycastle.util.encoders.Base64;
import org.w3c.dom.Document;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.dom.DOMStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.*;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import static java.util.Collections.singletonList;
import javax.xml.crypto.dsig.DigestMethod;

public class XmlSigner {
    static {
        UseBouncyCastle.please();
    }

    private static final String DIGEST_METHOD = DigestMethod.SHA256;
    private static final String SIGNATURE_METHOD = SignatureMethod.SHA256_RSA_MGF1;

    public static String signXMLDocument(String reportFilePath,
                                       String privateKeyFilePath,
                                       String certificateFilePath) throws Exception {
        try(
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            )
        {
            final DocumentBuilderFactory dbf = DocumentBuilderFactoryNoExternalEntities.newInstance();
            dbf.setNamespaceAware(true);

            final Document dipXmlDocument = dbf.newDocumentBuilder().parse(new FileInputStream(reportFilePath));
            final Document newDocument = dbf.newDocumentBuilder().newDocument();
            final XMLSignatureFactory xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");

            createDomSignContext(privateKeyFilePath, certificateFilePath, newDocument, xmlSignatureFactory, dipXmlDocument);

            final Result outputTarget = new StreamResult(outputStream);
            final TransformerFactory transformerFactory = TransformerFactoryExtension.newInstance();
            transformerFactory.newTransformer().transform(new DOMSource(newDocument), outputTarget);

            return outputStream.toString(StandardCharsets.UTF_8);
        }
        catch(final Exception e)
        {
            throw new Exception("Something wrong happened while signing the xml document.", e);
        }
    }

    private static void createDomSignContext( String privateKeyFilePath,
                                              String certificateFilePat,
            final Document newDocument,
            final XMLSignatureFactory xmlSignatureFactory,
            final Document dipXmlDocument
    )
            throws
            Exception {

        PrivateKey privateKey = getPrivateKey(privateKeyFilePath);
        X509Certificate cert = getCertificate(certificateFilePat);

        // Sign info
        final SignedInfo signedInfo = createSignedInfo(xmlSignatureFactory);
        // Key info
        final KeyInfo keyInfo = createKeyInfo(cert, xmlSignatureFactory);

        final DOMStructure content = new DOMStructure(dipXmlDocument.getDocumentElement());
        final XMLObject signedObject = xmlSignatureFactory.newXMLObject(Collections.singletonList(content), "object",
                null, null);

        final DOMSignContext domSignContext = new DOMSignContext(privateKey, newDocument);
        domSignContext.setDefaultNamespacePrefix("ds");

        final XMLSignature xmlSignature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo,
                Collections.singletonList(signedObject), null, null);
        xmlSignature.sign(domSignContext);
    }

    private static SignedInfo createSignedInfo(final XMLSignatureFactory xmlSignatureFactory)
            throws InvalidAlgorithmParameterException, NoSuchAlgorithmException
    {

        final Reference reference = xmlSignatureFactory.newReference("#object",
                xmlSignatureFactory.newDigestMethod(DIGEST_METHOD, null), List.of(), null, null);

        return xmlSignatureFactory.newSignedInfo(
                xmlSignatureFactory.newCanonicalizationMethod(
                        CanonicalizationMethod.INCLUSIVE,
                        (C14NMethodParameterSpec)null),
                xmlSignatureFactory.newSignatureMethod(SIGNATURE_METHOD, null),
                Collections.singletonList(reference)
        );
    }

    private static KeyInfo createKeyInfo(X509Certificate cert,
                                         final XMLSignatureFactory xmlSignatureFactory)
    {
        final KeyInfoFactory kif = xmlSignatureFactory.getKeyInfoFactory();

        final List<Object> x509Content = new ArrayList<>();
        x509Content.add(cert.getSubjectX500Principal().getName());
        x509Content.add(cert);

        final X509Data xd = kif.newX509Data(x509Content);
        return kif.newKeyInfo(singletonList(xd));
    }

    public static PrivateKey getPrivateKey(String privateKeyFilePath) throws Exception {
        String key = new String(Files.readAllBytes(new File(privateKeyFilePath).toPath()), Charset.defaultCharset());

        String privateKeyPEM = key
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replaceAll(System.lineSeparator(), "")
                .replace("-----END PRIVATE KEY-----", "");

        byte[] encoded = Base64.decode(privateKeyPEM);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
        return keyFactory.generatePrivate(keySpec);
    }

    public static X509Certificate getCertificate(String certificateFilePath) throws CertificateException, NoSuchProviderException, FileNotFoundException {
        CertificateFactory certFactory= CertificateFactory
                .getInstance("X.509", "BC");

        X509Certificate certificate = (X509Certificate) certFactory
                .generateCertificate(new FileInputStream(certificateFilePath));

        return certificate;
    }

    private static void dump(final Document doc, String filePath) throws TransformerException, FileNotFoundException {
        OutputStream os = new FileOutputStream(filePath);
        final TransformerFactory tf = TransformerFactory.newInstance();
        final Transformer trans = tf.newTransformer();
        trans.transform(new DOMSource(doc), new StreamResult(os));
    }
}
