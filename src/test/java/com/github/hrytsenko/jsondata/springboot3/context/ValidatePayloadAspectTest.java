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
import com.github.hrytsenko.jsondata.JsonParser;
import com.github.hrytsenko.jsondata.springboot3.ServiceException;
import com.github.hrytsenko.jsondata.springboot3.ValidatePayload;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ValidatePayloadAspectTest {

  ValidatePayloadAspect aspect;

  @BeforeEach
  void init() {
    aspect = new ValidatePayloadAspect();
  }

  @Test
  void onValidRequest() {
    String sourceSchema = """
        {
          "properties": {
            "foo": {
              "enum": ["FOO"]
            }
          },
          "required": ["foo"]
        }
        """;
    JsonBean sourceRequest = JsonParser.stringToEntity("{'foo':'FOO'}", JsonBean::create);

    JoinPoint sourceJoinPoint = mockJoinPoint(sourceRequest);
    ValidatePayload sourceConfig = Mockito.mock(ValidatePayload.class);
    Mockito.doReturn(sourceSchema)
        .when(sourceConfig).value();

    Assertions.assertDoesNotThrow(
        () -> aspect.handle(sourceJoinPoint, sourceConfig));
  }

  @Test
  void onInvalidRequest() {
    String sourceSchema = """
        {
          "properties": {
            "foo": {
              "enum": ["FOO"]
            }
          },
          "required": ["foo"]
        }
        """;
    JsonBean sourceRequest = JsonParser.stringToEntity("{'foo':'BAR'}", JsonBean::create);

    JoinPoint sourcePoint = mockJoinPoint(sourceRequest);
    ValidatePayload sourceConfig = Mockito.mock(ValidatePayload.class);
    Mockito.doReturn(sourceSchema)
        .when(sourceConfig).value();

    Assertions.assertThrows(ServiceException.BadRequest.class,
        () -> aspect.handle(sourcePoint, sourceConfig));
  }

  private JoinPoint mockJoinPoint(Object... arguments) {
    JoinPoint joinPoint = Mockito.mock(JoinPoint.class);
    Mockito.doReturn(arguments)
        .when(joinPoint).getArgs();
    return joinPoint;
  }

}
