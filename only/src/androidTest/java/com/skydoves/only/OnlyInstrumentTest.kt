/*
 * Copyright (C) 2019 skydoves
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

package com.skydoves.only

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class OnlyInstrumentTest {

  @Before
  fun initOnly() {
    Only.init(
      context = InstrumentationRegistry.getInstrumentation().targetContext,
      buildVersion = BuildConfig.VERSION_NAME
    )
  }

  @After
  fun clearAllOnly() {
    Only.clearAllOnly()
  }

  @Test
  fun onDoTest() {
    var count = 0
    Only.onDo("onDoTest", times = 1) {
      count++
    }

    assertThat(count, `is`(1))
    assertThat(Only.getOnlyTimes("onDoTest"), `is`(1))
  }

  @Test
  fun onDoOnceTest() {
    var count = 0

    for (i in 0..5) {
      Only.onDoOnce("onDoOnceTest", onDo = { count++ })
    }

    assertThat(count, `is`(1))
    assertThat(Only.getOnlyTimes("onDoOnceTest"), `is`(1))
  }

  @Test
  fun onDoTwiceTest() {
    var count = 0

    for (i in 0..5) {
      Only.onDoTwice("onDoTwiceTest", onDo = { count++ })
    }

    assertThat(count, `is`(2))
    assertThat(Only.getOnlyTimes("onDoTwiceTest"), `is`(2))
  }

  @Test
  fun onDoThriceTest() {
    var count = 0

    for (i in 0..5) {
      Only.onDoThrice("onDoThriceTest", onDo = { count++ })
    }

    assertThat(count, `is`(3))
    assertThat(Only.getOnlyTimes("onDoThriceTest"), `is`(3))
  }

  @Test
  fun onlyOnceTest() {
    var count = 0

    for (i in 0..5) {
      onlyOnce("onlyOnceTest") {
        onDo { count++ }
      }
    }

    assertThat(count, `is`(1))
    assertThat(Only.getOnlyTimes("onlyOnceTest"), `is`(1))
  }

  @Test
  fun onlyTwiceTest() {
    var count = 0

    for (i in 0..5) {
      onlyTwice("onlyTwiceTest") {
        onDo { count++ }
      }
    }

    assertThat(count, `is`(2))
    assertThat(Only.getOnlyTimes("onlyTwiceTest"), `is`(2))
  }

  @Test
  fun onlyTriceTest() {
    var count = 0

    for (i in 0..5) {
      onlyThrice("onlyTriceTest") {
        onDo { count++ }
      }
    }

    assertThat(count, `is`(3))
    assertThat(Only.getOnlyTimes("onlyTriceTest"), `is`(3))
  }

  @Test
  fun onDoTestWithTimes() {
    var count = 0

    for (i in 0..5) {
      Only.onDo("onDoTestWithTimes", times = 2) {
        count++
      }
    }

    assertThat(count, `is`(2))
    assertThat(Only.getOnlyTimes("onDoTestWithTimes"), `is`(2))
  }

  @Test
  fun onDoWithDoneTest() {
    var countOnDo = 0
    var countOnDone = 0

    for (i in 1..5) {
      only("onDoWithDoneTest", times = 3) {
        onDo { countOnDo++ }
        onDone { countOnDone++ }
      }
    }

    assertThat(countOnDo, `is`(3))
    assertThat(countOnDone, `is`(2))
    assertThat(Only.getOnlyTimes("onDoWithDoneTest"), `is`(3))
  }

  @Test
  fun onLastDoTest() {
    var countOnDo = 0
    var countOnLastDo = 0
    var countOnDone = 0

    for (i in 1..5) {
      only("onLastDoTest", times = 3) {
        onDo { countOnDo++ }
        onLastDo { countOnLastDo++ }
        onDone { countOnDone++ }
      }
    }

    assertThat(countOnDo, `is`(3))
    assertThat(countOnDone, `is`(2))
    assertThat(countOnLastDo, `is`(1))
    assertThat(Only.getOnlyTimes("onLastDoTest"), `is`(3))
  }

  @Test
  fun onBeforeDoneTest() {
    var countOnDo = 0
    var onBeforeDone = 0
    var countOnDone = 0

    for (i in 1..5) {
      only("onBeforeDoneTest", times = 3) {
        onDo { countOnDo++ }
        onBeforeDone { onBeforeDone++ }
        onDone { countOnDone++ }
      }
    }

    assertThat(countOnDo, `is`(3))
    assertThat(countOnDone, `is`(2))
    assertThat(onBeforeDone, `is`(1))
    assertThat(Only.getOnlyTimes("onBeforeDoneTest"), `is`(3))
  }

  @Test
  fun onLastDoWithOnBeforeDoneTest() {
    var countOnDo = 0
    var countOnLastDo = 0
    var onBeforeDone = 0
    var countOnDone = 0

    for (i in 1..5) {
      only("onLastDoWithOnBeforeDoneTest", times = 3) {
        onDo { countOnDo++ }
        onLastDo { countOnLastDo++ }
        onBeforeDone { onBeforeDone++ }
        onDone { countOnDone++ }
      }
    }

    assertThat(countOnDo, `is`(3))
    assertThat(countOnDone, `is`(2))
    assertThat(countOnLastDo, `is`(1))
    assertThat(onBeforeDone, `is`(1))
    assertThat(Only.getOnlyTimes("onLastDoWithOnBeforeDoneTest"), `is`(3))
  }

  @Test
  fun onDoWithVersionTest() {
    var countOnDo = 0
    var countOnDone = 0

    for (i in 1..5) {
      only("onDoWithVersionTest", times = 3) {
        onDo { countOnDo++ }
        onDone { countOnDone++ }
      }
    }

    for (i in 1..5) {
      only("onDoWithVersionTest", times = 3) {
        onDo { countOnDo-- }
        onDone { countOnDone-- }
        version("1.1.1.1")
      }
    }

    assertThat(countOnDo, `is`(0))
    assertThat(countOnDone, `is`(0))
    assertThat(Only.getOnlyTimes("onDoWithVersionTest"), `is`(3))
  }

  @Test
  fun markTest() {
    for (i in 1..5) {
      only("markTest", times = 3) {
        mark(i)
      }
    }
    assertThat(Only.getMarking("markTest"), `is`("1"))
    Only.clearOnly("markTest")

    for (i in 1..5) {
      only("markTest", times = 3) {
        onDo { Only.mark(name, "changedMarking") }
        mark(i)
      }
    }
    assertThat(Only.getMarking("markTest"), `is`("changedMarking"))

    Only.mark("markTest", "newMarking")
    assertThat(Only.getMarking("markTest"), `is`("newMarking"))
  }

  @Test
  fun builderTest() {
    var count = 0

    for (i in 1..3) {
      Only.Builder("builderTest", 3)
        .onDo { count++ }
        .run()
    }

    assertThat(count, `is`(3))
  }

  @Test
  fun sameOnlyName() {
    var count = 0

    onlyOnce("sameOnlyName") {
      onDo { count++ }
    }
    onlyOnce("sameOnlyName") {
      onDo { count++ }
    }
    onlyOnce("sameOnlyName") {
      onDo { count++ }
    }

    assertThat(count, `is`(1))
  }

  @Test
  fun clearOnlyTest() {
    var count = 0
    Only.onDo("clearOnlyTest", times = 1) {
      count++
    }

    assertThat(count, `is`(1))
    assertThat(Only.getOnlyTimes("clearOnlyTest"), `is`(1))

    Only.clearOnly("clearOnlyTest")
    assertThat(Only.getOnlyTimes("clearOnlyTest"), `is`(0))
  }

  @Test
  fun debugModeTest() {
    Only.init(
      context = InstrumentationRegistry.getInstrumentation().targetContext,
      buildVersion = BuildConfig.VERSION_NAME
    ).onlyOnDoDebugMode(true)

    var count = 0

    for (i in 1..5) {
      onlyOnce("debugModeTest") {
        onDo { count++ }
      }
    }

    assertThat(count, `is`(5))
    assertThat(Only.getOnlyTimes("debugModeTest"), `is`(0))
  }
}
