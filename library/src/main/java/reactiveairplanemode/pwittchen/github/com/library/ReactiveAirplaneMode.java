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

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;

/**
 * ReactiveAirplaneMode is an Android library listening airplane mode with RxJava Observables.
 */
public class ReactiveAirplaneMode {

  private static final String LOG_TAG = "ReactiveAirplaneMode";
  private static final String INTENT_EXTRA_STATE = "state";

  private ReactiveAirplaneMode() {
  }

  /**
   * Creates an instance of ReactiveAirplaneMode class
   *
   * @return ReactiveAirplaneMode object
   */
  public static ReactiveAirplaneMode create() {
    return new ReactiveAirplaneMode();
  }

  /**
   * Emits current state of the Airplane Mode in the beginning of the subscription
   * and then Observes Airplane Mode state of the device with BroadcastReceiver.
   * RxJava2 Observable emits true if the airplane mode turns on and false otherwise.
   *
   * @param context of the Application or Activity
   * @return RxJava2 Observable with Boolean value indicating state of the airplane mode
   */
  public Observable<Boolean> getAndObserve(final Context context) {
    return observe(context).startWith(isAirplaneModeOn(context));
  }

  /**
   * Observes Airplane Mode state of the device with BroadcastReceiver.
   * RxJava2 Observable emits true if the airplane mode turns on and false otherwise.
   *
   * @param context of the Application or Activity
   * @return RxJava2 Observable with Boolean value indicating state of the airplane mode
   */
  public Observable<Boolean> observe(final Context context) {
    checkContextIsNotNull(context);
    final IntentFilter filter = createIntentFilter();

    return Observable.create(new ObservableOnSubscribe<Boolean>() {
      @Override public void subscribe(@NonNull final ObservableEmitter<Boolean> emitter)
          throws Exception {
        final BroadcastReceiver receiver = createBroadcastReceiver(emitter);
        context.registerReceiver(receiver, filter);

        disposeInUiThread(new Action() {
          @Override public void run() throws Exception {
            tryToUnregisterReceiver(receiver, context);
          }
        });
      }
    });
  }

  @NonNull public BroadcastReceiver createBroadcastReceiver(
      @NonNull final ObservableEmitter<Boolean> emitter) {
    return new BroadcastReceiver() {
      @Override public void onReceive(final Context context, final Intent intent) {
        boolean isAirplaneModeOn = intent.getBooleanExtra(INTENT_EXTRA_STATE, false);
        emitter.onNext(isAirplaneModeOn);
      }
    };
  }

  @NonNull public IntentFilter createIntentFilter() {
    final IntentFilter filter = new IntentFilter();
    filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);
    return filter;
  }

  /**
   * Tries to unregister BroadcastReceiver.
   * Calls {@link #onError(java.lang.String, java.lang.Exception)} method
   * if receiver was already unregistered
   *
   * @param receiver BroadcastReceiver
   * @param context of the Application or Activity
   */
  public void tryToUnregisterReceiver(final BroadcastReceiver receiver, final Context context) {
    try {
      context.unregisterReceiver(receiver);
    } catch (Exception exception) {
      onError("receiver was already unregistered", exception);
    }
  }

  /**
   * Gets airplane mode state wrapped within a Single type
   *
   * @param context of the Application or Activity
   * @return RxJava2 Single with Boolean value indicating state of the airplane mode
   */
  public Single<Boolean> get(final Context context) {
    checkContextIsNotNull(context);
    return Single.create(new SingleOnSubscribe<Boolean>() {
      @Override public void subscribe(@NonNull SingleEmitter<Boolean> emitter) throws Exception {
        emitter.onSuccess(isAirplaneModeOn(context));
      }
    });
  }

  /**
   * Checks airplane mode once basing on the system settings.
   * Returns true if airplane mode is on or false otherwise.
   *
   * @param context of the Activity or Application
   * @return boolean value indicating state of the airplane mode.
   */
  public boolean isAirplaneModeOn(final Context context) {
    checkContextIsNotNull(context);
    String airplaneModeOnSetting;

    if (isAtLeastAndroidJellyBeanMr1()) {
      airplaneModeOnSetting = getAirplaneModeOnSettingGlobal();
    } else {
      airplaneModeOnSetting = getAirplaneModeOnSettingSystem();
    }

    return Settings.System.getInt(context.getContentResolver(), airplaneModeOnSetting, 0) == 1;
  }

  /**
   * Returns setting indicating airplane mode (for Android 17 and higher)
   *
   * @return String indicating airplane mode setting
   */
  @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1) private String getAirplaneModeOnSettingGlobal() {
    return Settings.Global.AIRPLANE_MODE_ON;
  }

  /**
   * Returns setting indicating airplane mode (for Android 16 and lower)
   *
   * @return String indicating airplane mode setting
   */
  @SuppressWarnings("deprecation") private String getAirplaneModeOnSettingSystem() {
    return Settings.System.AIRPLANE_MODE_ON;
  }

  /**
   * Handles errors which occurs within this class
   *
   * @param message with an error
   * @param exception which occurred
   */
  public void onError(final String message, final Exception exception) {
    Log.e(LOG_TAG, message, exception);
  }

  /**
   * Validation method, which checks if context of the Activity or Application is not null
   *
   * @param context of the Activity or application
   */
  public void checkContextIsNotNull(Context context) {
    if (context == null) {
      throw new IllegalArgumentException("context == null");
    }
  }

  /**
   * Validation method, which checks if current Android version is at least Jelly Bean MR1 (API 17)
   * or higher
   *
   * @return boolean true if current Android version is Jelly Bean MR1 or higher
   */
  public boolean isAtLeastAndroidJellyBeanMr1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
  }

  /**
   * Disposes an action in UI Thread
   *
   * @param dispose action to be executed
   * @return Disposable object
   */
  private Disposable disposeInUiThread(final Action dispose) {
    return Disposables.fromAction(new Action() {
      @Override public void run() throws Exception {
        if (Looper.getMainLooper() == Looper.myLooper()) {
          dispose.run();
        } else {
          final Scheduler.Worker inner = AndroidSchedulers.mainThread().createWorker();
          inner.schedule(new Runnable() {
            @Override public void run() {
              try {
                dispose.run();
              } catch (Exception exception) {
                onError("Could not unregister receiver in UI Thread", exception);
              }
              inner.dispose();
            }
          });
        }
      }
    });
  }
}
