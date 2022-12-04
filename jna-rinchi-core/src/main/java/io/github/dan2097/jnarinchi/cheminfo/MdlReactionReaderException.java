package io.github.dan2097.jnarinchi.cheminfo;

import java.util.Collections;
import java.util.List;

/**
 * This exception is thrown by {@link MdlReactionReader} if an error is encountered during reading.
 * <br>
 * All errors can be retrieved as a list of error strings with {@link #getErrors()} or
 * as a single string where individual errors are separated with a newline character
 * with {@link #getAllErrors()}.
 */
public class MdlReactionReaderException extends Exception {

  private static final long serialVersionUID = 1L;
  private final List<String> errors;
  
  /** {@inheritDoc} */
  public MdlReactionReaderException(List<String> errors) {
    super(getAllErrors(errors));
    this.errors = errors;
  }

  /**
   * Gets an list of generated error strings.
   * @return unmodifiable list of generated error strings
   */
  public List<String> getErrors() {
      return Collections.unmodifiableList(errors);
  }
  
  /**
   * Gets a single string with all errors.
   * @return single string with all errors where individual errors are separated by a newline character
   */
  public String getAllErrors() {
    return getMessage();
  }
  
  private static String getAllErrors(List<String> errors) {
    StringBuilder sb = new StringBuilder();
    for (String err : errors)
        sb.append(err).append("\n");
    return sb.toString();
  }

}
