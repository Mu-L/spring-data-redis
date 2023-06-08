/*
 * Copyright 2018-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.redis.cache;

import static org.assertj.core.api.Assertions.*;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.DirectFieldAccessor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.instrument.classloading.ShadowingClassLoader;
import org.springframework.lang.Nullable;

/**
 * Unit tests for {@link RedisCacheConfiguration}.
 *
 * @author Mark Paluch
 * @author Koy Zhuang
 */
class RedisCacheConfigurationUnitTests {

	@Test // DATAREDIS-763
	void shouldSetClassLoader() {

		ShadowingClassLoader classLoader = new ShadowingClassLoader(getClass().getClassLoader());

		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig(classLoader);

		Object adapter = new DirectFieldAccessor(config.getValueSerializationPair().getReader())
				.getPropertyValue("serializer");
		Object deserializerConverter = new DirectFieldAccessor(adapter).getPropertyValue("deserializer");
		Object deserializer = new DirectFieldAccessor(deserializerConverter).getPropertyValue("deserializer");
		Object usedClassLoader = new DirectFieldAccessor(deserializer).getPropertyValue("classLoader");

		assertThat(usedClassLoader).isSameAs(classLoader);
	}

	@Test // DATAREDIS-1032
	void shouldAllowConverterRegistration() {

		RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
		config.configureKeyConverters(registry -> registry.addConverter(new DomainTypeConverter()));

		assertThat(config.getConversionService().canConvert(DomainType.class, String.class)).isTrue();
	}

	@Test // GH-1433
	void shouldApplyTtlFunction() {

		RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl((key, value) -> Duration.ofSeconds((Integer) value + 10));

		assertThat(defaultCacheConfiguration.getTtlFunction().computeTimeToLive(Object.class, 10))
			.isEqualTo(Duration.ofSeconds(20));
		assertThat(defaultCacheConfiguration.getTtlFunction().computeTimeToLive(Object.class, 20))
			.isEqualTo(Duration.ofSeconds(30));
	}

	@Test // GH-1433
	void shouldApplyTtlFunctionUsingDuration() {

		RedisCacheConfiguration defaultCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig()
				.entryTtl(Duration.ofSeconds(10));

		assertThat(defaultCacheConfiguration.getTtlFunction().computeTimeToLive(Object.class, null))
				.isEqualTo(Duration.ofSeconds(10));
		assertThat(defaultCacheConfiguration.getTtlFunction().computeTimeToLive(Object.class, null))
				.isEqualTo(Duration.ofSeconds(10));
	}

	private static class DomainType {

	}

	static class DomainTypeConverter implements Converter<DomainType, String> {

		@Nullable
		@Override
		public String convert(DomainType source) {
			return null;
		}
	}
}
