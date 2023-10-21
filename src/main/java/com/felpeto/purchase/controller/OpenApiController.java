package com.felpeto.purchase.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.io.InputStream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Path("/v1/openapi")
@Produces(value = MediaType.APPLICATION_JSON)
@OpenAPIDefinition(
    info = @Info(
        title = "WEX - Purchase",
        version = "1",
        description = "This API helps manage all purchases made by WEX users",
        contact = @Contact(url = "https://wex.io", name = "WEX", email = "contato@wex.io")
    )
)
public class OpenApiController {

  @GET
  @Operation(hidden = true)
  public InputStream openApi() {
    return OpenApiController.class.getResourceAsStream("/openapi.json");
  }
}
