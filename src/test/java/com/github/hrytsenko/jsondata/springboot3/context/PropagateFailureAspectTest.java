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

import com.github.hrytsenko.jsondata.springboot3.ServiceException;
import com.github.hrytsenko.jsondata.springboot3.PropagateFailure;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class PropagateFailureAspectTest {

  PropagateFailureAspect aspect;

  @BeforeEach
  void init() {
    aspect = Mockito.spy(new PropagateFailureAspect());
  }

  @Test
  void onRegularExecution() {
    ProceedingJoinPoint sourcePoint = Mockito.mock(ProceedingJoinPoint.class);
    PropagateFailure sourceConfig = Mockito.mock(PropagateFailure.class);

    Assertions.assertDoesNotThrow(
        () -> aspect.handle(sourcePoint, sourceConfig));
  }

  @Test
  void onGenericException() {
    Exception sourceException = Mockito.mock(Exception.class);

    ProceedingJoinPoint sourcePoint = mockJoinPoint(sourceException);
    PropagateFailure sourceConfig = Mockito.mock(PropagateFailure.class);

    Assertions.assertThrows(ServiceException.ServerError.class,
        () -> aspect.handle(sourcePoint, sourceConfig));
  }

  @Test
  void onServiceException() {
    ServiceException.ServerError sourceException =
        new ServiceException.ServerError("INTERNAL_ERROR", "Execution failed.");

    ProceedingJoinPoint sourcePoint = mockJoinPoint(sourceException);
    PropagateFailure sourceConfig = Mockito.mock(PropagateFailure.class);

    ServiceException.ServerError actualException = Assertions.assertThrows(ServiceException.ServerError.class,
        () -> aspect.handle(sourcePoint, sourceConfig));
    Assertions.assertSame(sourceException, actualException);
  }

  @SneakyThrows
  private ProceedingJoinPoint mockJoinPoint(Exception exception) {
    ProceedingJoinPoint joinPoint = Mockito.mock(ProceedingJoinPoint.class);
    Mockito.doThrow(exception)
        .when(joinPoint).proceed();
    return joinPoint;
  }

}
