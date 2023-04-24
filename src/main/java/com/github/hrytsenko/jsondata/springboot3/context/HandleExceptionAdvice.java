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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
@AllArgsConstructor
class HandleExceptionAdvice {

  CorrelationSource correlationSource;

  @ExceptionHandler(Exception.class)
  ResponseEntity<JsonBean> onUnexpectedError(Exception exception) {
    log.error("Unexpected error", exception);
    return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR,
        new ServiceException.ServerError("SERVER_ERROR", null, exception));
  }

  @ExceptionHandler(ServiceException.BadRequest.class)
  ResponseEntity<JsonBean> onBadRequest(ServiceException.BadRequest exception) {
    log.error("Bad request", exception);
    return errorResponse(HttpStatus.BAD_REQUEST, exception);
  }

  @ExceptionHandler(ServiceException.Forbidden.class)
  ResponseEntity<JsonBean> onForbidden(ServiceException.Forbidden exception) {
    log.error("Forbidden", exception);
    return errorResponse(HttpStatus.FORBIDDEN, exception);
  }

  @ExceptionHandler(ServiceException.ServerError.class)
  ResponseEntity<JsonBean> onServerError(ServiceException.ServerError exception) {
    log.error("Server error", exception);
    return errorResponse(HttpStatus.INTERNAL_SERVER_ERROR, exception);
  }

  @ExceptionHandler(ServiceException.Unavailable.class)
  ResponseEntity<JsonBean> onUnavailable(ServiceException.Unavailable exception) {
    log.error("Unavailable", exception);
    return errorResponse(HttpStatus.SERVICE_UNAVAILABLE, exception);
  }

  private ResponseEntity<JsonBean> errorResponse(HttpStatus status, ServiceException exception) {
    JsonBean responseBody = JsonBean.create()
        .putString("error.correlation", correlationSource.getCorrelation())
        .putString("error.code", exception.getCode());
    if (exception.getMessage() != null) {
      responseBody.putString("error.message", exception.getMessage());
    }
    return ResponseEntity.status(status)
        .contentType(MediaType.APPLICATION_JSON)
        .body(responseBody);
  }

}
