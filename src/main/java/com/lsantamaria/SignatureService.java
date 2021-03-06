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
 * on the given transaction using the supplied private key and signature algorithm.
 */
public class SignatureService implements Service {

  private static final String SUPPORTED_KEY_ALGORITHM = "RSA";
  private static final String ALGORITHM_NOT_VALID = "The provided algorithm is not valid";
  private final Logger logger = LogManager.getLogger(this.getClass());

  @Override
  public CompletionStage<Optional<String>> processTransaction(Transaction tx) {
    PrivateKey privateKey = tx.getPrivateKey();
    String text = tx.getTextToSign();
    String signatureAlgorithm = tx.getSignatureAlgorithm();

    logger.debug("Processing element {}", tx.getTextToSign());

    return CompletableFuture.supplyAsync(() -> {
      if (!SUPPORTED_KEY_ALGORITHM.equals(privateKey.getAlgorithm())) {
        logger.info(
            "Signature transaction completed with a business error: Key algorithm not supported");
        return Optional.of(ALGORITHM_NOT_VALID);
      }
      try {
        Signature signature = Signature.getInstance(signatureAlgorithm);
        signature.initSign(privateKey);
        signature.update(text.getBytes());
        byte[] signed = signature.sign();
        logger.info("Signature transaction completed successfully. Signature: {}",
            Base64.getEncoder().encodeToString(signed));
        return Optional.empty();
      } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException e) {
        logger.error("Signature transaction completed with error");
        throw new ApplicationException(e);
      }
    });
  }
}
