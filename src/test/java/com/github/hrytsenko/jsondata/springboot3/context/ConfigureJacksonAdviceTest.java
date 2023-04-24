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

import static com.github.hrytsenko.jsondata.JsonParser.stringToEntity;
import static com.github.hrytsenko.jsondata.JsonParser.stringToMap;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.SimpleType;
import com.github.hrytsenko.jsondata.JsonBean;
import com.github.hrytsenko.jsondata.JsonEntity;
import java.util.Map;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class ConfigureJacksonAdviceTest {

  @Test
  @SneakyThrows
  void onSerializeEntity() {
    JsonSerializer<JsonEntity<?>> sourceSerializer = new ConfigureJacksonAdvice.JsonEntitySerializer();
    JsonGenerator sourceGenerator = Mockito.mock(JsonGenerator.class);

    sourceSerializer.serialize(stringToEntity("{'foo':'FOO'}", JsonBean::create), sourceGenerator, null);

    Map<String, ?> expectedObject = stringToMap("{'foo':'FOO'}");
    Mockito.verify(sourceGenerator)
        .writeObject(expectedObject);
  }

  @Test
  void onEnableSerializer() {
    BeanDescription sourceBean = Mockito.mock(BeanDescription.class);
    Mockito.doReturn(SimpleType.constructUnsafe(JsonBean.class))
        .when(sourceBean).getType();

    JsonSerializer<?> actualSerializer = new ConfigureJacksonAdvice.JsonEntitySerializerModifier()
        .modifySerializer(null, sourceBean, null);

    Assertions.assertEquals(ConfigureJacksonAdvice.JsonEntitySerializer.class, actualSerializer.getClass());
  }

  @Test
  @SneakyThrows
  void onDeserializeEntity() {
    JsonDeserializer<?> sourceDeserializer = new ConfigureJacksonAdvice.JsonEntityDeserializer(JsonBean.class);
    JsonParser sourceParser = new ObjectMapper().createParser("{\"foo\":\"FOO\"}");

    Object actualEntity = sourceDeserializer.deserialize(sourceParser, null);

    JsonBean expectedEntity = stringToEntity("{\"foo\":\"FOO\"}", JsonBean::create);
    Assertions.assertEquals(expectedEntity, actualEntity);
  }

  @Test
  void onEnableDeserializer() {
    BeanDescription sourceBean = Mockito.mock(BeanDescription.class);
    Mockito.doReturn(SimpleType.constructUnsafe(JsonBean.class))
        .when(sourceBean).getType();

    JsonDeserializer<?> actualDeserializer = new ConfigureJacksonAdvice.JsonEntityDeserializerModifier()
        .modifyDeserializer(null, sourceBean, null);

    Assertions.assertEquals(ConfigureJacksonAdvice.JsonEntityDeserializer.class, actualDeserializer.getClass());
  }

}
