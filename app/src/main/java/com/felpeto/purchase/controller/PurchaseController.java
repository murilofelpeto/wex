package com.felpeto.purchase.controller;

import static com.felpeto.purchase.controller.mapper.PurchaseMapper.toPurchaseResponseDto;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.Response.Status.CREATED;

import com.felpeto.purchase.controller.dto.request.PurchaseRequestDto;
import com.felpeto.purchase.controller.dto.response.PurchaseResponseDto;
import com.felpeto.purchase.controller.handler.dto.ErrorResponseDto;
import com.felpeto.purchase.controller.mapper.PurchaseMapper;
import com.felpeto.purchase.service.PurchaseService;
import io.quarkus.security.Authenticated;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Authenticated
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
              description = "Return the saved purchase",
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
  @RolesAllowed("admin")
  public Response save(@Valid @NotNull final PurchaseRequestDto purchaseRequestDto) {
    log.info("Saving purchase {}", purchaseRequestDto);
    final var purchase = PurchaseMapper.toPurchase(purchaseRequestDto);
    final var response = service.save(purchase);
    return Response.status(CREATED)
        .entity(toPurchaseResponseDto(response))
        .build();
  }

  @Operation(
      summary = "Retrive a purchase",
      responses = {
          @ApiResponse(
              responseCode = "200",
              description = "Return the purchase",
              content =
              @Content(
                  mediaType = APPLICATION_JSON,
                  schema = @Schema(implementation = PurchaseResponseDto.class))),
          @ApiResponse(
              responseCode = "404",
              description = "Entity not found",
              content =
              @Content(
                  mediaType = APPLICATION_JSON,
                  schema = @Schema(implementation = ErrorResponseDto.class)))
      })
  @GET
  @Path("/{uuid}")
  @Consumes(APPLICATION_JSON)
  @Produces(APPLICATION_JSON)
  @RolesAllowed("admin")
  public Response get(@PathParam("uuid") final UUID uuid,
      @QueryParam("country") @NotBlank(message = "Country is mandatory") final String country) {
    log.info("Retrieving purchase {}", uuid);
    final var response = service.getPurchaseTransaction(uuid, country);
    return Response.ok(toPurchaseResponseDto(response)).build();

  }
}
