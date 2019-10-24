import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;

import com.lsantamaria.SignatureService;
import com.lsantamaria.Transaction;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.util.Optional;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.CountDownLatch;
import org.junit.Test;

public class SignatureServiceTest {

  private static final String SIGNATURE_ALGORITHM = "SHA1withRSA";
  private static final String KEY_ALGORITHM = "RSA";
  private static final String NOT_SUPPORTED_KEY_ALGORITHM = "DSA";
  private final SignatureService signatureService = new SignatureService();

  @Test
  public void givenTransactionSupportedKeyAlg_whenProcessingIt_thenReturnSuccessfulResult()
      throws Exception {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    String lineToSign = "This is the line to sign";
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    PrivateKey privateKey = keyPairGenerator.generateKeyPair().getPrivate();
    Transaction transaction = new Transaction(privateKey, lineToSign, SIGNATURE_ALGORITHM);

    CompletionStage<Optional<String>> result = signatureService.processTransaction(transaction);

    Optional<String> expectedResult = Optional.empty();
    var actualResult = new Object() {
      Optional<String> value;
    };
    result
        .thenAccept(transactionResult -> {
          actualResult.value = transactionResult;
          countDownLatch.countDown();
        });

    countDownLatch.await();

    assertThat(expectedResult, is(actualResult.value));
  }

  @Test
  public void givenTransactionNotSupportedKeyAlg_whenProcessingIt_thenReturnSuccessfulResult()
      throws Exception {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    String lineToSign = "This is the line to sign";
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(NOT_SUPPORTED_KEY_ALGORITHM);
    PrivateKey privateKey = keyPairGenerator.generateKeyPair().getPrivate();
    Transaction transaction = new Transaction(privateKey, lineToSign, SIGNATURE_ALGORITHM);

    CompletionStage<Optional<String>> result = signatureService.processTransaction(transaction);
    var actualResult = new Object() {
      Optional<String> value;
    };

    result
        .thenAccept(transactionResult -> {
          actualResult.value = transactionResult;
          countDownLatch.countDown();
        });

    countDownLatch.await();

    assertThat(actualResult.value.isEmpty(), is(false));
  }

  @Test
  public void givenTransactionInvalidSigAlg_whenProcessingIt_thenThrowApplicationException()
      throws Exception {
    CountDownLatch countDownLatch = new CountDownLatch(1);
    String lineToSign = "";
    KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM);
    PrivateKey privateKey = keyPairGenerator.generateKeyPair().getPrivate();
    Transaction transaction = new Transaction(privateKey, lineToSign, "Invalid");

    CompletionStage<Optional<String>> result = signatureService.processTransaction(transaction);

    var exceptionIsThrown = new Object() {
      boolean value;
    };

    result
        .exceptionally(e -> {
          exceptionIsThrown.value = true;
          countDownLatch.countDown();
          return Optional.empty();
        });

    countDownLatch.await();

    assertThat(exceptionIsThrown.value, is(true));
  }
}
