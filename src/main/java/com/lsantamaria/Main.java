package com.lsantamaria;

import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

/**
 * Main client of the stream processor. This example streams the lines of a file, processes them in
 * form of transactions and attaches the results to a sink.
 */
public class Main {

  public static void main(String[] args) throws NoSuchAlgorithmException, URISyntaxException {
    Service signatureService = new SignatureService();
    StreamProcessor<Transaction, TransactionResult> streamProcessor =
        new TransactionProcessor(signatureService);

    String fileName = "bible.txt";
    URI fileUri = ClassLoader.getSystemClassLoader().getResource(fileName).toURI();
    Path filePath = Paths.get(fileUri);

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    PrivateKey privateKey = keyPairGenerator.generateKeyPair().getPrivate();

    final ActorSystem system = ActorSystem.create("transaction-processor");
    final Materializer materializer = ActorMaterializer.create(system);

    FileIO.fromPath(filePath)
        .via(Framing.delimiter(ByteString.fromString("\n"), 256,
            FramingTruncation.ALLOW).map(ByteString::utf8String))
        .map(line -> new Transaction(privateKey, line))
        .via(streamProcessor.process())
        .to(Sink.ignore())
        .run(materializer);
  }
}
