/*
 * Copyright (C) 2020 Anton Hrytsenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.hrytsenko.jsondata.springboot3.context;

import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.springboot3.CorrelationSource;
import com.github.hrytsenko.jsondata.springboot3.ServiceException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

class HandleExceptionAdviceTest {

  private static final String CORRELATION = "CORRELATION";

  HandleExceptionAdvice advice;

  @BeforeEach
  void init() {
    CorrelationSource correlationSource = Mockito.mock(CorrelationSource.class);
    Mockito.doReturn(CORRELATION)
        .when(correlationSource).getCorrelation();
    advice = new HandleExceptionAdvice(correlationSource);
  }

  @Test
  void onUnknownError() {
    ResponseEntity<?> actualResponse = advice.onUnexpectedError(
        Mockito.mock(Exception.class));

    assertResponse(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", actualResponse);
  }

  @Test
  void onBadRequest() {
    ResponseEntity<?> actualResponse = advice.onBadRequest(
        new ServiceException.BadRequest("BAD_REQUEST"));

    assertResponse(HttpStatus.BAD_REQUEST, "BAD_REQUEST", actualResponse);
  }

  @Test
  void onForbidden() {
    ResponseEntity<?> actualResponse = advice.onForbidden(
        new ServiceException.Forbidden("FORBIDDEN"));

    assertResponse(HttpStatus.FORBIDDEN, "FORBIDDEN", actualResponse);
  }

  @Test
  void onServerError() {
    ResponseEntity<?> actualResponse = advice.onServerError(
        new ServiceException.ServerError("SERVER_ERROR"));

    assertResponse(HttpStatus.INTERNAL_SERVER_ERROR, "SERVER_ERROR", actualResponse);
  }

  @Test
  void onUnavailable() {
    ResponseEntity<?> actualResponse = advice.onUnavailable(
        new ServiceException.Unavailable("UNAVAILABLE"));

    assertResponse(HttpStatus.SERVICE_UNAVAILABLE, "UNAVAILABLE", actualResponse);
  }

  static void assertResponse(HttpStatus expectedStatus, String expectedCode, ResponseEntity<?> actualResponse) {
    HttpStatusCode actualStatus = actualResponse.getStatusCode();
    Assertions.assertEquals(expectedStatus, actualStatus);
    MediaType actualContentType = actualResponse.getHeaders().getContentType();
    Assertions.assertEquals(MediaType.APPLICATION_JSON, actualContentType);

    JsonBean actualBody = (JsonBean) actualResponse.getBody();
    Assertions.assertNotNull(actualBody);
    String actualCorrelation = actualBody.getString("error.correlation");
    Assertions.assertEquals(CORRELATION, actualCorrelation);
    String actualCode = actualBody.getString("error.code");
    Assertions.assertEquals(expectedCode, actualCode);
  }

}
