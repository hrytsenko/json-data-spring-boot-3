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

import com.github.hrytsenko.jsondata.springboot3.PropagateFailure;
import com.github.hrytsenko.jsondata.springboot3.ServiceException;
import lombok.SneakyThrows;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
class PropagateFailureAspect {

  @Around("@annotation(config)")
  @SneakyThrows
  Object handle(ProceedingJoinPoint point, PropagateFailure config) {
    try {
      return point.proceed();
    } catch (ServiceException exception) {
      throw exception;
    } catch (Exception exception) {
      throw new ServiceException.ServerError(config.value(), "Execution failed.", exception);
    }
  }

}
