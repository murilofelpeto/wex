package com.felpeto.purchase.controller.configuration;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.inject.Singleton;
import lombok.NoArgsConstructor;

@Singleton
@NoArgsConstructor(access = PRIVATE)
public class ObjectMapperConfiguration {

  public static void customize(final ObjectMapper om) {
    om.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    om.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    om.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    om.registerModule(new JavaTimeModule());
  }
}
