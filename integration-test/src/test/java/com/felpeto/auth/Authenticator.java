package com.felpeto.auth;

import static io.restassured.RestAssured.given;

public class Authenticator {

  public static Integer port;

  public static String admin() {
    return generator("wex-admin", "wex-admin");
  }

  public static String user() {
    return generator("user", "user");
  }

  private static String generator(final String username, final String password) {
    return "Bearer " + given()
        .auth()
        .preemptive()
        .basic("backend", "FOJHmE8A3ckjM5mANHLBAE72PbFIyzED")
        .contentType("application/x-www-form-urlencoded")
        .formParam("grant_type", "password")
        .formParam("client_id", "backend")
        .formParam("client_secret", "FOJHmE8A3ckjM5mANHLBAE72PbFIyzED")
        .formParam("username", username)
        .formParam("password", password)
        .when()
        .post("http://localhost:" + port + "/realms/wex/protocol/openid-connect/token")
        .then()
        .extract()
        .response()
        .jsonPath()
        .getString("access_token");
  }
}
