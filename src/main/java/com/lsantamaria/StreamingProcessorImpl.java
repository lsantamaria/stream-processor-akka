package com.lsantamaria;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.stream.ActorMaterializer;
import akka.stream.Materializer;
import akka.stream.javadsl.Flow;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;

/**
 * Streaming service that processes {@link Transaction} messages asynchronously and generates {@link
 * TransactionResult} messages.
 */
public class StreamingProcessorImpl implements StreamingProcessor {

  private final Service service;

  public StreamingProcessorImpl(Service service) {
    this.service = service;
  }

  public void streamFile(String fileName) throws NoSuchAlgorithmException {
    final ActorSystem system = ActorSystem.create("transaction-processor");
    final Materializer materializer = ActorMaterializer.create(system);
    final KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
    final PrivateKey privateKey = keyPairGenerator.generateKeyPair().getPrivate();

    final Flow<Transaction, TransactionResult, NotUsed> processor =
        Flow.of(Transaction.class)
            .map(service::processTransaction)
            .map(TransactionResult::new);

    Source
        .range(0, 100)
        .map(number -> new Transaction(privateKey, Integer.toString(number)))
        .async()
        .via(processor)
        .runWith(Sink.ignore(),
            materializer);

//    FileIO.fromPath(Paths.get(fileName))
//        .via(Framing.delimiter
//            (ByteString.fromString("\n"), 256,
//                FramingTruncation.ALLOW).map(ByteString::utf8String))
//        .map(line -> {
//          System.out.println(line);
//          return new Transaction(privateKey, line);
//        })
//        .async()
//
//        .via(processor)
//        .to(Sink.ignore())
//        .run(materializer);
  }
}
