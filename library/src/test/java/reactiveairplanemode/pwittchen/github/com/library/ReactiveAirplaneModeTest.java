/*
 * Copyright (C) 2017 Piotr Wittchen
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package reactiveairplanemode.pwittchen.github.com.library;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class) public class ReactiveAirplaneModeTest {

  @Test public void reactiveAirplaneModeObjectShouldNotBeNull() {
    assertThat(ReactiveAirplaneMode.create()).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getAndObserveShouldThrowAnExceptionForNullContext() {
    ReactiveAirplaneMode.create().getAndObserve(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeShouldThrowAnExceptionForNullContext() {
    ReactiveAirplaneMode.create().observe(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShouldThrowAnExceptionForNullContext() {
    ReactiveAirplaneMode.create().get(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void isAirplaneModeOnShouldThrowAnExceptionForNullContext() {
    ReactiveAirplaneMode.create().isAirplaneModeOn(null);
  }

  @Test public void observeShouldCreateIntentFilter() {
    // given
    ReactiveAirplaneMode reactiveAirplaneMode = spy(ReactiveAirplaneMode.create());

    // when
    reactiveAirplaneMode.observe(RuntimeEnvironment.application);

    // then
    verify(reactiveAirplaneMode).createIntentFilter();
  }
}