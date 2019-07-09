package com.skydoves.only

import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [21])
class OnlyUnitTest {

  @Before
  @Suppress("DEPRECATION")
  fun initOnly() {
    Only.init(
      context = RuntimeEnvironment.application,
      buildVersion = BuildConfig.VERSION_NAME)
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

    MatcherAssert.assertThat(count, CoreMatchers.`is`(1))
    MatcherAssert.assertThat(Only.getOnlyTimes("onDoTest"), CoreMatchers.`is`(1))
  }

  @Test
  fun onDoOnceTest() {
    var count = 0

    for (i in 0..5) {
      Only.onDoOnce("onDoOnceTest", onDo = { count++ })
    }

    MatcherAssert.assertThat(count, CoreMatchers.`is`(1))
    MatcherAssert.assertThat(Only.getOnlyTimes("onDoOnceTest"), CoreMatchers.`is`(1))
  }

  @Test
  fun onDoTwiceTest() {
    var count = 0

    for (i in 0..5) {
      Only.onDoTwice("onDoTwiceTest", onDo = { count++ })
    }

    MatcherAssert.assertThat(count, CoreMatchers.`is`(2))
    MatcherAssert.assertThat(Only.getOnlyTimes("onDoTwiceTest"), CoreMatchers.`is`(2))
  }

  @Test
  fun onDoThriceTest() {
    var count = 0

    for (i in 0..5) {
      Only.onDoThrice("onDoThriceTest", onDo = { count++ })
    }

    MatcherAssert.assertThat(count, CoreMatchers.`is`(3))
    MatcherAssert.assertThat(Only.getOnlyTimes("onDoThriceTest"), CoreMatchers.`is`(3))
  }

  @Test
  fun onlyOnceTest() {
    var count = 0

    for (i in 0..5) {
      onlyOnce("onlyOnceTest") {
        onDo { count++ }
      }
    }

    MatcherAssert.assertThat(count, CoreMatchers.`is`(1))
    MatcherAssert.assertThat(Only.getOnlyTimes("onlyOnceTest"), CoreMatchers.`is`(1))
  }

  @Test
  fun onlyTwiceTest() {
    var count = 0

    for (i in 0..5) {
      onlyTwice("onlyTwiceTest") {
        onDo { count++ }
      }
    }

    MatcherAssert.assertThat(count, CoreMatchers.`is`(2))
    MatcherAssert.assertThat(Only.getOnlyTimes("onlyTwiceTest"), CoreMatchers.`is`(2))
  }

  @Test
  fun onlyTriceTest() {
    var count = 0

    for (i in 0..5) {
      onlyThrice("onlyTriceTest") {
        onDo { count++ }
      }
    }

    MatcherAssert.assertThat(count, CoreMatchers.`is`(3))
    MatcherAssert.assertThat(Only.getOnlyTimes("onlyTriceTest"), CoreMatchers.`is`(3))
  }

  @Test
  fun onDoTestWithTimes() {
    var count = 0

    for (i in 0..5) {
      Only.onDo("onDoTestWithTimes", times = 2) {
        count++
      }
    }

    MatcherAssert.assertThat(count, CoreMatchers.`is`(2))
    MatcherAssert.assertThat(Only.getOnlyTimes("onDoTestWithTimes"), CoreMatchers.`is`(2))
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

    MatcherAssert.assertThat(countOnDo, CoreMatchers.`is`(3))
    MatcherAssert.assertThat(countOnDone, CoreMatchers.`is`(2))
    MatcherAssert.assertThat(Only.getOnlyTimes("onDoWithDoneTest"), CoreMatchers.`is`(3))
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

    MatcherAssert.assertThat(countOnDo, CoreMatchers.`is`(3))
    MatcherAssert.assertThat(countOnDone, CoreMatchers.`is`(2))
    MatcherAssert.assertThat(countOnLastDo, CoreMatchers.`is`(1))
    MatcherAssert.assertThat(Only.getOnlyTimes("onLastDoTest"), CoreMatchers.`is`(3))
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

    MatcherAssert.assertThat(countOnDo, CoreMatchers.`is`(3))
    MatcherAssert.assertThat(countOnDone, CoreMatchers.`is`(2))
    MatcherAssert.assertThat(onBeforeDone, CoreMatchers.`is`(1))
    MatcherAssert.assertThat(Only.getOnlyTimes("onBeforeDoneTest"), CoreMatchers.`is`(3))
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

    MatcherAssert.assertThat(countOnDo, CoreMatchers.`is`(3))
    MatcherAssert.assertThat(countOnDone, CoreMatchers.`is`(2))
    MatcherAssert.assertThat(countOnLastDo, CoreMatchers.`is`(1))
    MatcherAssert.assertThat(onBeforeDone, CoreMatchers.`is`(1))
    MatcherAssert.assertThat(Only.getOnlyTimes("onLastDoWithOnBeforeDoneTest"), CoreMatchers.`is`(3))
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

    MatcherAssert.assertThat(countOnDo, CoreMatchers.`is`(0))
    MatcherAssert.assertThat(countOnDone, CoreMatchers.`is`(0))
    MatcherAssert.assertThat(Only.getOnlyTimes("onDoWithVersionTest"), CoreMatchers.`is`(3))
  }

  @Test
  fun markTest() {
    for (i in 1..5) {
      only("markTest", times = 3) {
        mark(i)
      }
    }
    MatcherAssert.assertThat(Only.getMarking("markTest"), CoreMatchers.`is`("1"))
    Only.clearOnly("markTest")

    for (i in 1..5) {
      only("markTest", times = 3) {
        onDo { Only.mark(name, "changedMarking") }
        mark(i)
      }
    }
    MatcherAssert.assertThat(Only.getMarking("markTest"), CoreMatchers.`is`("changedMarking"))

    Only.mark("markTest", "newMarking")
    MatcherAssert.assertThat(Only.getMarking("markTest"), CoreMatchers.`is`("newMarking"))
  }

  @Test
  fun builderTest() {
    var count = 0

    for (i in 1..3) {
      Only.Builder("builderTest", 3)
        .onDo { count++ }
        .run()
    }

    MatcherAssert.assertThat(count, CoreMatchers.`is`(3))
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

    MatcherAssert.assertThat(count, CoreMatchers.`is`(1))
  }

  @Test
  fun clearOnlyTest() {
    var count = 0
    Only.onDo("clearOnlyTest", times = 1) {
      count++
    }

    MatcherAssert.assertThat(count, CoreMatchers.`is`(1))
    MatcherAssert.assertThat(Only.getOnlyTimes("clearOnlyTest"), CoreMatchers.`is`(1))

    Only.clearOnly("clearOnlyTest")
    MatcherAssert.assertThat(Only.getOnlyTimes("clearOnlyTest"), CoreMatchers.`is`(0))
  }

  @Test
  @Suppress("DEPRECATION")
  fun debugModeTest() {
    Only.init(
      context = RuntimeEnvironment.application,
      buildVersion = BuildConfig.VERSION_NAME).onlyOnDoDebugMode(true)

    var count = 0

    for (i in 1..5) {
      onlyOnce("debugModeTest") {
        onDo { count++ }
      }
    }

    MatcherAssert.assertThat(count, CoreMatchers.`is`(5))
    MatcherAssert.assertThat(Only.getOnlyTimes("debugModeTest"), CoreMatchers.`is`(0))
  }
}