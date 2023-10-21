package com.felpeto.purchase.model.vo;

import com.felpeto.purchase.model.exceptions.InvalidStringFormatException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

@Getter
@ToString
@EqualsAndHashCode
public final class Description {

  private static final String MANDATORY_FIELD = "Description is mandatory";
  private static final String FIELD = "Description.value";
  private static final String TARGET = Description.class.getSimpleName();
  private static final String VIOLATION_MESSAGE = "The Description name must not be blank or null";

  private final String value;

  private Description(final String value) {
    this.value = value;
  }

  public static Description of(final String value) {
    if (StringUtils.isBlank(value)) {
      throw new InvalidStringFormatException(MANDATORY_FIELD,
          FIELD,
          TARGET,
          FIELD,
          VIOLATION_MESSAGE);
    }
    return new Description(value);
  }
}
