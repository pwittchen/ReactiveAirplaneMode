ReactiveAirplaneMode
====================
✈️ Android library listening airplane mode with RxJava Observables

This library is compatible with RxJava2 and RxAndroid2.

JavaDoc can be found at: https://pwittchen.github.io/ReactiveAirplaneMode/.

Contents
--------

- [Usage](#usage)
- [Examples](#examples)
- [Download](#download)
- [Tests](#tests)
- [Code style](#code-style)
- [Static code analysis](#static-code-analysis)
- [Changelog](#changelog)
- [Releasing](#releasing)
- [License](#license)

Usage
-----

We can observe airplane mode in the following way:

```java
ReactiveAirplaneMode.observe(context)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(isOn -> textView.setText(String.format("Airplane mode on: %s", isOn.toString())));
```

When airplane mode changes, subscriber will be notified with appropriate `Boolean` value (`true` if airplane mode is on or `false` otherwise).

If you're using this code in an `Activity`, don't forget to dispose `Disposable` in `onPause()` method just like in the sample app.

Please note that method above **will be called only when the Airplane mode changes**.

If you want to **read airplane mode on start and then observe it**, you can use `getAndObserve(context)` method as follows:

```java
ReactiveAirplaneMode.getAndObserve(context)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(isOn -> textView.setText(String.format("Airplane mode on: %s", isOn.toString())));
```

If you want to **check airplane mode only once**, you can use `get(context)` method, which returns `Single<Boolean>` value:

```java
ReactiveAirplaneMode.get(context)
    .subscribeOn(Schedulers.io())
    .observeOn(AndroidSchedulers.mainThread())
    .subscribe(isOn -> textView.setText(String.format("Airplane mode on: %s", isOn.toString())));
```

If you want to check airplane mode **only once without using Reactive Streams**, just call `isAirplaneModeOn(context)` method:

```java
boolean isOn = ReactiveAirplaneMode.isAirplaneModeOn(context);
```

Examples
--------

Exemplary application is located in `app` directory of this repository.

Download
--------

TBD.

Library will be published on Maven Central Repository soon. Please, stay tuned.

Tests
-----

Tests are available in `library/src/test/java/` directory and can be executed on JVM without any emulator or Android device from Android Studio or CLI with the following command:

```
./gradlew test
```

To generate test coverage report, run the following command:

```
./gradlew test jacocoTestReport
```

Code style
----------

Code style used in the project is called `SquareAndroid` from Java Code Styles repository by Square available at: https://github.com/square/java-code-styles.

Static code analysis
--------------------

Static code analysis runs Checkstyle, FindBugs, PMD and Lint. It can be executed with command:

```
./gradlew check
```

Reports from analysis are generated in library/build/reports/ directory.

Changelog
---------

See [CHANGELOG.md](https://github.com/pwittchen/ReactiveAirplaneMode/blob/master/CHANGELOG.md) file.

Releasing
---------

See [RELEASING.md](https://github.com/pwittchen/ReactiveAirplaneMode/blob/master/RELEASING.md) file.

References
----------
- https://stackoverflow.com/questions/4319212/how-can-one-detect-airplane-mode-on-android
- https://stackoverflow.com/questions/5533881/toggle-airplane-mode-in-android

License
-------

    Copyright 2017 Piotr Wittchen

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
