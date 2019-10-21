package com.lsantamaria;


import java.security.NoSuchAlgorithmException;

public interface StreamingProcessor {
  void streamFile(String fileName) throws NoSuchAlgorithmException;
}
