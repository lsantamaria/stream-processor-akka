package com.lsantamaria;

import java.util.Optional;
import java.util.concurrent.CompletionStage;
import lombok.AllArgsConstructor;
import lombok.Value;


/**
 * The transaction result contains information about the returned value of {@link
 * Service#processTransaction(Transaction)}:
 *
 * <p>
 * Successful future holding {@link Optional#empty()} if the operation completed successfully.
 *
 * Successful future holding {@link Optional#of(Object)} if the operation failed because of a
 * business error detailed in the errorMsg string.
 *
 * Failed future if the CompletionStage complete exceptionally
 * </>
 */
@AllArgsConstructor
@Value
public class TransactionResult {
  private CompletionStage<Optional<String>> result;
}
