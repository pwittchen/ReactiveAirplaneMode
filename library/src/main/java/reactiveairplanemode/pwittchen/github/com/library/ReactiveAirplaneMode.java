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
import android.os.Build;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import io.reactivex.functions.Action;

/**
 * ReactiveAirplaneMode is an Android library listening airplane mode with RxJava Observables.
 */
public class ReactiveAirplaneMode {

  private static final String LOG_TAG = "ReactiveAirplaneMode";
  private static final String INTENT_ACTION_AIRPLANE_MODE = "android.intent.action.AIRPLANE_MODE";
  private static final String INTENT_EXTRA_STATE = "state";

  //TODO: check if that works
  public Observable<Boolean> observe(final Context context) {
    final IntentFilter filter = new IntentFilter(INTENT_ACTION_AIRPLANE_MODE);
    filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);

    return Observable.create(new ObservableOnSubscribe<Boolean>() {
      @Override public void subscribe(@NonNull final ObservableEmitter<Boolean> emitter)
          throws Exception {
        final BroadcastReceiver receiver = new BroadcastReceiver() {
          @Override public void onReceive(final Context context, final Intent intent) {
            boolean isAirplaneModeOn = intent.getBooleanExtra(INTENT_EXTRA_STATE, false);
            emitter.onNext(isAirplaneModeOn);
          }
        };

        context.registerReceiver(receiver, filter);

        unsubscribeInUiThread(new Action() {
          @Override public void run() throws Exception {
            tryToUnregisterReceiver(receiver, context);
          }
        });
      }
    }).defaultIfEmpty(isAirplaneModeOn(context));
  }

  public void tryToUnregisterReceiver(final BroadcastReceiver receiver, final Context context) {
    try {
      context.unregisterReceiver(receiver);
    } catch (Exception exception) {
      onError("receiver was already unregistered", exception);
    }
  }

  @SuppressWarnings("deprecation") public boolean isAirplaneModeOn(final Context context) {
    String airplaneModeOn;

    boolean isAtLeastAndroid17 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;

    if (isAtLeastAndroid17) {
      airplaneModeOn = Settings.Global.AIRPLANE_MODE_ON;
    } else {
      airplaneModeOn = Settings.System.AIRPLANE_MODE_ON;
    }

    return Settings.System.getInt(context.getContentResolver(), airplaneModeOn, 0) == 0;
  }

  public void onError(final String message, final Exception exception) {
    Log.e(LOG_TAG, message, exception);
  }

  private Disposable unsubscribeInUiThread(final Action unsubscribe) {
    return Disposables.fromAction(new Action() {
      @Override public void run() throws Exception {
        if (Looper.getMainLooper() == Looper.myLooper()) {
          unsubscribe.run();
        } else {
          final Scheduler.Worker inner = AndroidSchedulers.mainThread().createWorker();
          inner.schedule(new Runnable() {
            @Override public void run() {
              try {
                unsubscribe.run();
              } catch (Exception e) {
                onError("Could not unregister receiver in UI Thread", e);
              }
              inner.dispose();
            }
          });
        }
      }
    });
  }
}
