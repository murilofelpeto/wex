package com.felpeto.purchase.controller;

import static com.felpeto.purchase.controller.mapper.PurchaseMapper.toPurchaseResponseDto;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CREATED;

import com.felpeto.purchase.controller.dto.request.PurchaseRequestDto;
import com.felpeto.purchase.controller.dto.response.PurchaseResponseDto;
import com.felpeto.purchase.controller.handler.dto.ErrorResponseDto;
import com.felpeto.purchase.controller.mapper.PurchaseMapper;
import com.felpeto.purchase.service.PurchaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ApplicationScoped
@Path("/v1/purchase")
public class PurchaseController {

  private final PurchaseService service;

  public PurchaseController(final PurchaseService service) {
    this.service = service;
  }

  @Operation(
      summary = "Save a new purchase",
      responses = {
          @ApiResponse(
              responseCode = "201",
              description = "Return the created owner",
              content =
              @Content(
                  mediaType = APPLICATION_JSON,
                  schema = @Schema(implementation = PurchaseResponseDto.class))),
          @ApiResponse(
              responseCode = "422",
              description = "Unprocessable Entity",
              content =
              @Content(
                  mediaType = APPLICATION_JSON,
                  schema = @Schema(implementation = ErrorResponseDto.class)))
      })
  @POST
  @Consumes(APPLICATION_JSON)
  @Produces(APPLICATION_JSON)
  public Response save(@Valid @NotNull final PurchaseRequestDto purchaseRequestDto) {
    log.info("Saving purchase {}", purchaseRequestDto);
    final var purchase = PurchaseMapper.toPurchase(purchaseRequestDto);
    final var response = service.save(purchase);
    return Response.status(CREATED)
        .entity(toPurchaseResponseDto(response))
        .build();
  }
}
