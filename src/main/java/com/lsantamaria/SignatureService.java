package com.lsantamaria;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.SignatureException;
import java.util.Base64;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Example implementation of {@link Service} interface. This implementation digitally signs the text
 * on the given transaction using the supplied private key.
 */
public class SignatureService implements Service {

  private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
  private static final String SUPPORTED_KEY_ALGORITHM = "RSA";
  private static final String ALGORITHM_NOT_VALID = "The provided algorithm is not valid";
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Override
  public CompletionStage<Optional<String>> processTransaction(Transaction tx) {
    PrivateKey privateKey = tx.getPrivateKey();
    String text = tx.getTextToSign();
    logger.info("Processing element {}", tx.getTextToSign());
    return CompletableFuture.supplyAsync(() -> {
      if (!SUPPORTED_KEY_ALGORITHM.equals(privateKey.getAlgorithm())) {
        logger.info("Transaction completed with a business error {}", text);
        return Optional.of(ALGORITHM_NOT_VALID);
      }

      try {
        Signature signature = Signature.getInstance(SIGNATURE_ALGORITHM);
        signature.initSign(privateKey);
        signature.update(text.getBytes());
        byte[] signed = signature.sign();
        logger.info("Transaction completed successfully. Element {}, Signature: {}", text,
            Base64.getEncoder().encodeToString(signed));
        return Optional.empty();
      } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
        logger.error("Transaction completed with a signature exception {}", text);
        throw new ApplicationException(
            String.format("Exception completing transaction with element %s ", text));
      }
    });

  }
}
