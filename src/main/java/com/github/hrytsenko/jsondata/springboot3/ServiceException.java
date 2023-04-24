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
package com.github.hrytsenko.jsondata.springboot3;

import lombok.Getter;

@Getter
public sealed class ServiceException extends RuntimeException {

  String code;

  private ServiceException(String code, String message, Throwable cause) {
    super(message, cause);
    this.code = code;
  }

  public static final class BadRequest extends ServiceException {

    public BadRequest(String code) {
      this(code, null, null);
    }

    public BadRequest(String code, String message) {
      this(code, message, null);
    }

    public BadRequest(String code, String message, Throwable cause) {
      super(code, message, cause);
    }

  }

  public static final class Forbidden extends ServiceException {

    public Forbidden(String code) {
      this(code, null, null);
    }

    public Forbidden(String code, String message) {
      this(code, message, null);
    }

    public Forbidden(String code, String message, Throwable cause) {
      super(code, message, cause);
    }

  }

  public static final class ServerError extends ServiceException {

    public ServerError(String code) {
      this(code, null, null);
    }

    public ServerError(String code, String message) {
      this(code, message, null);
    }

    public ServerError(String code, String message, Throwable cause) {
      super(code, message, cause);
    }

  }

  public static final class Unavailable extends ServiceException {

    public Unavailable(String code) {
      this(code, null, null);
    }

    public Unavailable(String code, String message) {
      this(code, message, null);
    }

    public Unavailable(String code, String message, Throwable cause) {
      super(code, message, cause);
    }

  }

}
