# Only

<p align="center">
  <a href="https://opensource.org/licenses/Apache-2.0"><img alt="License" src="https://img.shields.io/badge/License-Apache%202.0-blue.svg"/></a>
  <a href="https://android-arsenal.com/api?level=15"><img alt="API" src="https://img.shields.io/badge/API-15%2B-brightgreen.svg?style=flat"/></a>
  <a href="https://www.codacy.com/app/skydoves/Only?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=skydoves/Only&amp;utm_campaign=Badge_Grade"><img alt="API" src="https://api.codacy.com/project/badge/Grade/3e04de5613974807a8eede8ccebc0a7d"/></a>
  <a href="https://travis-ci.org/skydoves/Only"><img alt="Build Status" src="https://travis-ci.org/skydoves/Only.svg?branch=master"/></a><br>
  <a href="https://androidweekly.net/issues/issue-370"><img alt="Javadoc" src="https://img.shields.io/badge/Android%20Weekly-%23370-orange.svg"/></a>
  <a href="https://us12.campaign-archive.com/?u=f39692e245b94f7fb693b6d82&id=b12259cf32"><img alt="KotlinWeekly" src="https://img.shields.io/badge/KotlinWeekly-%23159-4E71E6"/></a>
  <a href="https://skydoves.github.io/libraries/only/javadoc/only/com.skydoves.only/index.html"><img alt="Javadoc" src="https://img.shields.io/badge/Javadoc-Only-yellow.svg"/></a>
</p>

<p align="center">
:bouquet: An easy way to persist and run code block only as many times as necessary on Android.
</p>

<p align="center">
<img src="https://github.com/skydoves/Only/blob/master/art/showcase.png" width="720" height="307"/>
</p>

## Download
[![Download](https://api.bintray.com/packages/devmagician/maven/only/images/download.svg)](https://bintray.com/devmagician/maven/only/_latestVersion)
[![Jitpack](https://jitpack.io/v/skydoves/Only.svg)](https://jitpack.io/#skydoves/Only)

### Gradle
Add a dependency code to your **module**'s `build.gradle` file.
```gradle
dependencies {
    implementation "com.github.skydoves:only:1.0.4"
}
```

## Usage
### Initialize
First, initialize the `Only` using `init()` method like below. <br>
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
  onDo { showIntroPopup() }
}
```

### onDone
Below codes will run the `doSomethingAfterDone()` and `toast("done")` after run the `onDo` block codes three times.

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

### onLastDo, onBeforeDone
We can do pre and post-processing using `onLastDo`, `onBeforeDone` options.
```kotlin
only("Intro", times = 3) {
  onDo {
    showIntroPopup()
    toast("onDo only three times")
  }
  onLastDo { // executes only once after finished onDo block 3 times.
    toast("finished onDo")
  }
  onBeforeDone { // executes only once before run onDone block.
    toast("starts onDo")
  }
  onDone {
    doSomethingAfterDone()
    toast("done")
  }
}
```
We can apply it for repeating x times.<br>
Below codes shows review-popup 3 times and checks the user reviewed or not in `onLastDo` block.<br>
If not, clear times using the `Only.clearOnly` method, and repeat it the first time again.
```kotlin
only("Intro", times = 3) {
  onDo { showReviewRequestPopup() }
  onLastDo { // executes only once after finished onDo block 3 times.
    if (!isRequested) {
      Only.clearOnly(this@only.name)
    }
  }
}
```

### Version Control
We can renew the persistence times for controlling the version using `version` option. <br>
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

### Create Using Builder
We can run Only using `Only.Builder` class like below.
```kotlin
Only.Builder("introPopup", times = 3)
  .onDo { showIntroPopup() }
  .onDone { doSomethingAfterDone() }
  .version("1.1.1.1")
  .run()
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

### Clear Data
We can optionally delete the stored `Only` target data or delete the whole `Only` data.
```kotlin
Only.clearOnly("introPopup") // clears a saved target Only data.
Only.clearAllOnly() // clears all of the target Only data on the application.
```

### View Extension
Below codes will show the `button` view only once.
```kotlin
button.onlyVisibility(name = "myButton", times = 1, visible = true)
```

### Toast Extension
Below codes will show toast only x times.
```kotlin
onlyToast("toast", 3, "This toast will be shown only three times.")
onlyOnceToast("toast1", "This toast will be shown only once.")
onlyTwiceToast("toast2", "This toast will be shown only twice.")
onlyThriceToast("toast3", "This toast will be shown only thrice.")
```

### Marking
We can mark data to the Only target.
```kotlin
only("introPopup", times = 3) {
  onDo { showIntroPopup() }
  onDone { doSomethingAfterDone() }
  mark("abc") // marks only once when run by kotlin dsl or builder class.
}

Only.mark("introPopup", 3) // changes marking using mark method.
val marking = Only.getMarking("introPopup") // gets the marked data.
```

### Debug Mode
Sometimes on debug, we don't need to persist data and replay onDone block. <br>
`onlyOnDoDebugMode` helps that ignore persistence data and onDone block when initialization. It runs only onDo block.
```kotlin
Only.init(this)
   .onlyOnDoDebugMode(true)
```

## Usage in Java
Here are some usages for Java developers.
```java
int times = Only.INSTANCE.getOnlyTimes("IntroPopup") ;
if (times < 3) {
    Only.INSTANCE.setOnlyTimes("IntroPopup", times + 1);
    showIntroPopup();
}
```
Or we can run `Only` in java project using `Only.Builder` and `Function0`.
```java
new Only.Builder("introPopup", 1)
    .onDo(new Function0<Unit>() {
      @Override
      public Unit invoke() {
        doSomethingOnlyOnce();
        return Unit.INSTANCE;
      }
    })
    .onDone(new Function0<Unit>() {
      @Override
      public Unit invoke() {
        doSOmethingAfterDone();
        return Unit.INSTANCE;
      }
    }).run(); // run the Only
```

## Find this library useful? :heart:
Support it by joining __[stargazers](https://github.com/skydoves/only/stargazers)__ for this repository. :star:

# License
```xml
Copyright 2019 skydoves (Jaewoong Eum)

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
