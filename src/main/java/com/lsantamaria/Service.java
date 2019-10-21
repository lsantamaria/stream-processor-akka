package com.lsantamaria;

import java.util.Optional;
import java.util.concurrent.CompletionStage;

/**
 * This contract defines the operation performed for each {@link Transaction} message.
 */
public interface Service {

  /**
   * Process a transaction asynchronously and return the {@link CompletionStage} that will hold the
   * result.
   *
   * @param tx the transaction
   */
  CompletionStage<Optional<String>> processTransaction(Transaction tx);
}
