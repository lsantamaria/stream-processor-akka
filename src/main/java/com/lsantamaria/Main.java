package com.lsantamaria;

import java.security.NoSuchAlgorithmException;

public class Main {

  public static void main(String[] args) throws NoSuchAlgorithmException {
    final Service signatureService = new SignatureService();
    final StreamingProcessor streamingProcessor =
        new StreamingProcessorImpl(signatureService);
    final String filename = "src/main/resources/bible.txt";
    streamingProcessor.streamFile(filename);
  }
}
