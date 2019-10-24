package com.lsantamaria;

import akka.NotUsed;
import akka.stream.javadsl.Flow;


/**
 * A stream processor that takes an stream of {@link Transaction} messages asynchronously and
 * generates as output {@link TransactionResult} messages.
 */
public class TransactionProcessor implements StreamProcessor<Transaction, TransactionResult> {

  private final Service service;

  public TransactionProcessor(Service service) {
    this.service = service;
  }

  @Override
  public Flow<Transaction, TransactionResult, NotUsed> process() {
    return Flow.of(Transaction.class)
            .map(service::processTransaction)
            .map(TransactionResult::new);
  }
}
