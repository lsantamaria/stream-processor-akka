package com.lsantamaria;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.FileIO;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Framing;
import akka.stream.javadsl.FramingTruncation;
import akka.stream.javadsl.Sink;
import akka.util.ByteString;
import java.net.URI;
import java.nio.file.Paths;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;

/**
 * A stream processor is a program which takes as input one or more streams of events/messages, runs
 * some computation for each event, and generates output in the form of other events, side effects,
 * or a combination of the two.
 *
 * This program runs a streaming processor which processes {@link Transaction} messages
 * asynchronously and generates {@link TransactionResult} messages. The streaming processor is
 * modeled as a {@link Flow} that receives a streamed file, processes it and send the results to a
 * simple sink that ignores them.
 */
public class StreamProcessorProgram {

  public static void main(String[] args) throws Exception {
    Service signatureService = new SignatureService();

    String fileName = "bible.txt";
    String signatureAlgorithm = "SHA1withRSA";
    URI file = Thread.currentThread().getContextClassLoader().getResource(fileName).toURI();

    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    PrivateKey privateKey = keyPairGenerator.generateKeyPair().getPrivate();

    final ActorSystem system = ActorSystem.create("transaction-processor");
    final Materializer materializer = ActorMaterializer.create(system);

    Flow<Transaction, TransactionResult, NotUsed> streamProcessor =
        Flow.of(Transaction.class)
            .map(signatureService::processTransaction)
            .map(TransactionResult::new);

    FileIO.fromPath(Paths.get(file))
        .via(Framing.delimiter(ByteString.fromString("\n"), 256,
            FramingTruncation.ALLOW).map(ByteString::utf8String))
        .map(line -> new Transaction(privateKey, line, signatureAlgorithm))
        .via(streamProcessor)
        .to(Sink.ignore())
        .run(materializer);
  }
}
