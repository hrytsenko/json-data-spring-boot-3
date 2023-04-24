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

import com.github.hrytsenko.jsondata.JsonEntity;
import com.github.hrytsenko.jsondata.JsonValidator;
import com.github.hrytsenko.jsondata.JsonValidatorException;
import com.github.hrytsenko.jsondata.springboot3.ServiceException;
import com.github.hrytsenko.jsondata.springboot3.ValidatePayload;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
class ValidatePayloadAspect {

  @Before("@annotation(config)")
  void handle(JoinPoint point, ValidatePayload config) {
    var target = (JsonEntity<?>) point.getArgs()[0];

    try {
      JsonValidator.create(config.value()).validate(target);
    } catch (JsonValidatorException exception) {
      throw new ServiceException.BadRequest("INVALID_REQUEST", "Validation failed.");
    }
  }

}
