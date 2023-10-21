package com.felpeto.purchase.controller.configuration;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalSerializer extends JsonSerializer<BigDecimal> {

  @Override
  public void serialize(final BigDecimal value,
      final JsonGenerator jsonGenerator,
      final SerializerProvider provider)
      throws IOException {

    jsonGenerator.writeString(value.setScale(2, RoundingMode.HALF_UP).toString());
  }
}
