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
import java.nio.file.Path;
import java.security.PrivateKey;
import java.util.Objects;

/**
 * This is a basic implementation of a stream processor that reads from a test file, sign its
 * content and writes the results to a sink that discards the elements.
 */
public class StreamingProcessorImpl implements StreamingProcessor {

  private final Service service;

  public StreamingProcessorImpl(Service service) {
    this.service = service;
  }

  public void processFile(Path filePath, PrivateKey privateKey) {
    Objects.requireNonNull(filePath, "File path can not be null");
    Objects.requireNonNull(privateKey, "Private key can not be null");

    final ActorSystem system = ActorSystem.create("transaction-processor");
    final Materializer materializer = ActorMaterializer.create(system);

    final Flow<Transaction, TransactionResult, NotUsed> processor =
        Flow.of(Transaction.class)
            .map(service::processTransaction)
            .map(TransactionResult::new);

    FileIO.fromPath(filePath)
        .via(Framing.delimiter(ByteString.fromString("\n"), 256,
            FramingTruncation.ALLOW).map(ByteString::utf8String))
        .map(line -> new Transaction(privateKey, line))
        .async()
        .via(processor)
        .to(Sink.ignore())
        .run(materializer);
  }
}
