package com.lsantamaria;


import java.nio.file.Path;
import java.security.PrivateKey;

/**
 * Streaming service that processes {@link Transaction} messages asynchronously and generates {@link
 * TransactionResult} messages. The transactions are generated using the lines of the file
 * provided.
 *
 * @param filePath the path of the file to process.
 * @param privateKey the private key for signing the file.
 */
public interface StreamingProcessor {

  void processFile(Path filePath, PrivateKey privateKey);
}
