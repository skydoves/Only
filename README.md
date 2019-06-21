# Only

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=15"><img alt="API" src="https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://travis-ci.org/skydoves/Only"><img alt="Build Status" src="https://travis-ci.org/skydoves/Only.svg?branch=master"/></a>
    <a href="https://skydoves.github.io/libraries/only/javadoc/only/com.skydoves.only/index.html"><img alt="Javadoc" src="https://img.shields.io/badge/Javadoc-Only-yellow.svg"/></a>
</p>

<p align="center">
An easy way to persistence and run block codes only as many times as necessary on Android.
</p>

<p align="center">
<img src="https://github.com/skydoves/Only/blob/master/art/showcase.png" width="800" height="417"/>
</p>

## Download
[![Download](https://api.bintray.com/packages/devmagician/maven/only/images/download.svg)](https://bintray.com/devmagician/maven/only/_latestVersion)
[![Jitpack](https://jitpack.io/v/skydoves/Only.svg)](https://jitpack.io/#skydoves/Only)

### Gradle
And add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "com.github.skydoves:only:1.0.0"
}
```

## Usage
### Initialize
Fisrt, initialize the `Only` using `init()` method like below. <br>
This code can be initialized on `Application` class only once.
```kotlin
Only.init(context)
```

### onDo

Below codes will run the `showIntroPopup()` only three times using `onDo` method.
```kotlin
Only.onDo("introPopup", times = 3) {
  showIntroPopup()
}

// kotlin dsl
only("introPopup", times = 3) {
  onDo {
    showIntroPopup()
  }
}
```

### onDone
Below codes will run the `doSomeThingAfterDone()` and `toast("done")` after run the `onDo` block codes three times.

```kotlin
Only.onDo("introPopup", times = 3,
  onDo = {
    showIntroPopup()
    toast("onDo only three times")
  },
  onDone = {
    doSomethingAfterDone()
    toast("done")
  })

// kotlin dsl
only("introPopup", times = 3) {
  onDo {
    showIntroPopup()
    toast("onDo only three times")
   }
  onDone {
    doSomeThingAfterDone()
    toast("done")
  }
}
```

### Version Control
`version` helps renew the run times for control the version. <br>
If the version is different from the old version, run times will be initialized 0.<br>

```kotlin
Only.onDo("introPopup", times = 3,
  onDo = { showIntroPopup() },
  onDone = { doSomethingAfterDone() },
  version = "1.1.1.1"
)

// kotlin dsl
only("introPopup", times = 3) {
  onDo { showIntroPopup() }
  onDone { doSomethingAfterDone() }
  version("1.1.1.1")
}
```

### OnlyOnce, OnlyTwice, OnlyThrice
Here is some useful kotlin-dsl functions.

```kotlin
onlyOnce("onlyOnce") { // run the onDo block codes only once.
  onDo { doSomethingOnlyOnce() }
  onDone { doSomethingAfterDone() }
}

onlyTwice("onlyTwice") { // run the onDo block codes only twice.
  onDo { doSomethingOnlyTwice() }
  onDone { doSomethingAfterDone() }
}

onlyThrice("onlyThrice") { // run the onDo block codes only three times.
  onDo { doSomethingOnlyThrice() }
  onDone { doSomethingAfterDone() }
  version("1.1.1.1")
}
```

### Clear times
You can optionally delete the stored `Only` times data or delete the entire `Only` times data.
```kotlin
Only.clearOnly("introPopup") // clear one saved times data.
Only.clearAllOnly() // clear all of the times data on the application.
```

### View Extension
Below codes will show the `button` view only once.
```kotlin
button.onlyVisibility(name = "myButton", times = 1, visible = true)
```

## Usage in Java
You can use `Only` in java project using `Only.Builder` and `Function0`.
```java
new Only.Builder("introPopup", 1).onDo(new Function0<Unit>() {
  @Override
  public Unit invoke() {
    doSomethingOnlyOnce()
    return Unit.INSTANCE;
  }
}).onDone(new Function0<Unit>() {
  @Override
  public Unit invoke() {
    doSOmethingAfterDone()
    return Unit.INSTANCE;
  }
}).run(); // run the Only
```

## Find this library useful? :heart:
Support it by joining __[stargazers](https://github.com/skydoves/only/stargazers)__ for this repository. :star:

# License
```xml
Copyright 2019 skydoves

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
