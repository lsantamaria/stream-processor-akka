package com.lsantamaria;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

/**
 * Main client of the stream processor. It creates a private key and
 */
public class Main {

  public static void main(String[] args) throws NoSuchAlgorithmException, URISyntaxException {
    Service signatureService = new SignatureService();
    StreamingProcessor streamingProcessor = new StreamingProcessorImpl(signatureService);

    String fileName = "bible.txt";
    URI fileUri = ClassLoader.getSystemClassLoader().getResource(fileName).toURI();
    Path filePath = Paths.get(fileUri);

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    PrivateKey privateKey = keyPairGenerator.generateKeyPair().getPrivate();

    streamingProcessor.processFile(filePath, privateKey);
  }
}
