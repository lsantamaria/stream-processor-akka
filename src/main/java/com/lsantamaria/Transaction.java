package com.lsantamaria;

import java.security.PrivateKey;
import lombok.AllArgsConstructor;
import lombok.Value;

@AllArgsConstructor
@Value
public class Transaction{
  private PrivateKey privateKey;
  private String textToSign;
}
