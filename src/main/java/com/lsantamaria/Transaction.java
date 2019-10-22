package com.lsantamaria;

import java.security.PrivateKey;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * A signature transaction. It contains the text to sign and the private key that will be used for
 * that.
 */
@AllArgsConstructor
@Value
public class Transaction {

  private PrivateKey privateKey;
  private String textToSign;
}
