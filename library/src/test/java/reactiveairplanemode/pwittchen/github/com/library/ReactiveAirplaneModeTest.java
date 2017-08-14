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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import io.reactivex.Observable;
import io.reactivex.Single;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class) public class ReactiveAirplaneModeTest {

  private Context context;
  private ReactiveAirplaneMode reactiveAirplaneMode;

  @Before public void setUp() {
    context = spy(RuntimeEnvironment.application.getApplicationContext());
    reactiveAirplaneMode = spy(ReactiveAirplaneMode.create());
  }

  @Test public void reactiveAirplaneModeObjectShouldNotBeNull() {
    assertThat(reactiveAirplaneMode).isNotNull();
  }

  @Test(expected = IllegalArgumentException.class)
  public void getAndObserveShouldThrowAnExceptionForNullContext() {
    reactiveAirplaneMode.getAndObserve(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void observeShouldThrowAnExceptionForNullContext() {
    reactiveAirplaneMode.observe(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void getShouldThrowAnExceptionForNullContext() {
    reactiveAirplaneMode.get(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void isAirplaneModeOnShouldThrowAnExceptionForNullContext() {
    reactiveAirplaneMode.isAirplaneModeOn(null);
  }

  @Test public void observeShouldCreateIntentFilter() {
    // when
    reactiveAirplaneMode.observe(context);

    // then
    verify(reactiveAirplaneMode).createIntentFilter();
  }

  @Test public void isAirplaneModeOnShouldReturnFalseByDefault() {
    // when
    final boolean isAirplaneModeOn = reactiveAirplaneMode.isAirplaneModeOn(context);

    // then
    assertThat(isAirplaneModeOn).isFalse();
  }

  @Test public void getAndObserveShouldEmitAirplaneModeOffByDefault() {
    // when
    Observable<Boolean> observable = reactiveAirplaneMode.getAndObserve(context);

    // then
    assertThat(observable.blockingFirst()).isFalse();
  }

  @Test public void getShouldEmitAirplaneModeOffByDefault() {
    // when
    Single<Boolean> single = reactiveAirplaneMode.get(context);

    // then
    assertThat(single.blockingGet()).isFalse();
  }

  @Test public void shouldCreateIntentFilter() {
    // when
    final IntentFilter intentFilter = reactiveAirplaneMode.createIntentFilter();

    // then
    assertThat(intentFilter).isNotNull();
  }

  @Test public void shouldCreateIntentFilterWithAirplaneModeChangedAction() {
    // when
    final IntentFilter intentFilter = reactiveAirplaneMode.createIntentFilter();

    // then
    assertThat(intentFilter.getAction(0)).isEqualTo(Intent.ACTION_AIRPLANE_MODE_CHANGED);
  }

  @Test public void shouldTryToUnregisterReceiver() {
    // given
    final BroadcastReceiver broadcastReceiver = mock(BroadcastReceiver.class);

    // when
    reactiveAirplaneMode.tryToUnregisterReceiver(broadcastReceiver, context);

    // then
    verify(context).unregisterReceiver(broadcastReceiver);
  }
}