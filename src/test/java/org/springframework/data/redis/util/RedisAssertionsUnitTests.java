/*
 * Copyright 2017-2023 the original author or authors.
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
 *  limitations under the License.
 */
package org.springframework.data.redis.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * Unit Tests for {@link RedisAssertions}.
 *
 * @author John Blum
 * @see org.junit.jupiter.api.Test
 * @see org.springframework.data.redis.util.RedisAssertions
 * @since 1.0.0
 */
@ExtendWith(MockitoExtension.class)
public class RedisAssertionsUnitTests {

	@Mock
	private Supplier<String> mockSupplier;

	@Test
	public void requireObjectWithMessageAndArgumentsIsSuccessful() {
		assertThat(RedisAssertions.requireObject("test", "Test message")).isEqualTo("test");
	}

	@Test
	public void requireObjectWithMessageAndArgumentsThrowsIllegalArgumentException() {

		assertThatIllegalArgumentException()
			.isThrownBy(() -> RedisAssertions.requireObject(null, "This is a %s", "test"))
			.withMessage("This is a test")
			.withNoCause();
	}

	@Test
	public void requireObjectWithSupplierIsSuccessful() {

		assertThat(RedisAssertions.requireObject("mock", this.mockSupplier)).isEqualTo("mock");

		verifyNoInteractions(this.mockSupplier);
	}

	@Test
	public void requireObjectWithSupplierThrowsIllegalArgumentException() {

		doReturn("Mock message").when(this.mockSupplier).get();

		assertThatIllegalArgumentException()
			.isThrownBy(() -> RedisAssertions.requireObject(null, this.mockSupplier))
			.withMessage("Mock message")
			.withNoCause();

		verify(this.mockSupplier, times(1)).get();
		verifyNoMoreInteractions(this.mockSupplier);
	}
}
