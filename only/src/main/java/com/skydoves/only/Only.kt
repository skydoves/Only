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

@file:Suppress("unused")

package com.skydoves.only

import android.content.Context
import android.content.SharedPreferences
import android.content.pm.ApplicationInfo
import androidx.annotation.RestrictTo
import androidx.annotation.VisibleForTesting

@DslMarker
annotation class OnlyDsl

/** Run [Only] by [Only.Builder] using kotlin dsl. */
@OnlyDsl
inline fun only(name: String, times: Int, block: Only.Builder.() -> Unit): Unit =
  Only.Builder(name, times).apply(block).run()

/** Run only once [Only] by [Only.Builder] using kotlin dsl. */
@OnlyDsl
inline fun onlyOnce(name: String, times: Int = 1, block: Only.Builder.() -> Unit): Unit =
  Only.Builder(name, times).apply(block).run()

/** Run only twice [Only] by [Only.Builder] using kotlin dsl. */
@OnlyDsl
inline fun onlyTwice(name: String, times: Int = 2, block: Only.Builder.() -> Unit): Unit =
  Only.Builder(name, times).apply(block).run()

/** Run only thrice [Only] by [Only.Builder] using kotlin dsl. */
@OnlyDsl
inline fun onlyThrice(name: String, times: Int = 3, block: Only.Builder.() -> Unit): Unit =
  Only.Builder(name, times).apply(block).run()

/** Easy way to run block codes only as many times as necessary. */
object Only {

  @JvmStatic
  private lateinit var preference: SharedPreferences
  private lateinit var buildVersion: String
  private var isDebuggable = 0
  private var doOnDebugMode = false

  /** initialize the Only default properties. */
  @JvmStatic
  fun init(context: Context): Only {
    val info = context.packageManager.getPackageInfo(context.packageName, 0)
    init(context, info.versionName)
    return this@Only
  }

  /** initialize the Only default properties. */
  @JvmStatic
  @VisibleForTesting
  fun init(context: Context, buildVersion: String): Only {
    this.preference = context.applicationContext.getSharedPreferences("Only", Context.MODE_PRIVATE)
    this.isDebuggable = context.applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
    this.buildVersion = buildVersion
    return this@Only
  }

  /** run onDo using [Only.Builder]. */
  private fun runByBuilder(builder: Builder) {
    if (getOnlyTimes(builder.name) == 0) {
      mark(builder.name, builder.marking)
    }
    onDo(builder.name, builder.times, builder.onDo, builder.onDone, builder.onLastDo, builder.onBeforeDone, builder.version)
  }

  /** check debugging mode. */
  @JvmStatic
  fun onlyOnDoDebugMode(ignore: Boolean): Only {
    this.doOnDebugMode = ignore
    return this@Only
  }

  /** check debug mode. */
  @JvmStatic
  fun isDebugMode(): Boolean {
    return doOnDebugMode && isDebuggable != 0
  }

  /** execute the onDo block only as many times as necessary. */
  @JvmStatic
  inline fun onDo(
    name: String,
    times: Int,
    crossinline onDo: () -> Unit
  ): Only {

    onDo(name, times, onDo, { })
    return this@Only
  }

  /**
   * execute the onDo block only as many times as necessary.
   * if unnecessary, unCatch block will be executed.
   */
  @JvmStatic
  inline fun onDo(
    name: String,
    times: Int,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit
  ): Only {

    onDo(name, times, onDo, onDone, { }, { })
    return this@Only
  }

  /**
   * execute the onDo block only as many times as necessary.
   * if the version is different from the old version, Only times will be initialized 0.
   */
  @JvmStatic
  inline fun onDo(
    name: String,
    times: Int,
    crossinline onDo: () -> Unit,
    version: String = ""
  ): Only {

    affectVersion(name, version)
    onDo(name, times, onDo, { }, { }, { })

    return this@Only
  }

  /**
   * execute the onDo block only as many times as necessary.
   * if unnecessary, unCatch block will be executed.
   * if the version is different from the old version, Only times will be initialized 0.
   */
  @JvmStatic
  inline fun onDo(
    name: String,
    times: Int,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit,
    version: String = ""
  ): Only {

    affectVersion(name, version)
    onDo(name, times, onDo, onDone, { }, { })
    return this@Only
  }

  /**
   * execute the onDo block only as many times as necessary.
   * if unnecessary, unCatch block will be executed.
   * if the version is different from the old version, Only times will be initialized 0.
   * onLastDo block will be run only once after the last run time of the onDo.
   * onBeforeDone block will be run only once before the onDone block will be run.
   */
  @JvmStatic
  inline fun onDo(
    name: String,
    times: Int,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit,
    crossinline onLastDo: () -> Unit = {},
    crossinline onBeforeDone: () -> Unit = {}
  ): Only {

    // run only onDo block when debug mode.
    if (isDebugMode()) {
      onDo()
      return this@Only
    }

    val persistCode = getOnlyTimes(name)
    if (persistCode < times) {
      setOnlyTimes(name, persistCode + 1)
      onDo()

      if (persistCode == times - 1) {
        onLastDo()
      }
    } else if (persistCode >= times) {
      if (!getOnBeforeDoneExecuted(name)) {
        setOnBeforeDoneExecuted(name)
        onBeforeDone()
      }
      onDone()
    }
    return this@Only
  }

  /**
   * execute the onDo block only as many times as necessary with onLastDo and onBeforeDone.
   * if unnecessary, unCatch block will be executed.
   * if the version is different from the old version, Only times will be initialized 0.
   */
  @JvmStatic
  inline fun onDo(
    name: String,
    times: Int,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit,
    crossinline onLastDo: () -> Unit = {},
    crossinline onBeforeDone: () -> Unit = {},
    version: String = ""
  ): Only {

    affectVersion(name, version)
    onDo(name, times, onDo, onDone, onLastDo, onBeforeDone)
    return this@Only
  }

  /** execute the onDo block only once. */
  @JvmStatic
  inline fun onDoOnce(
    name: String,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit = {},
    version: String = ""
  ): Only {

    onDo(name, 1, onDo, onDone, version)
    return this@Only
  }

  /** execute the onDo block only once with onLastDo and onBeforeDone. */
  @JvmStatic
  inline fun onDoOnce(
    name: String,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit = {},
    crossinline onLastDo: () -> Unit = {},
    crossinline onBeforeDone: () -> Unit = {},
    version: String = ""
  ): Only {

    onDo(name, 1, onDo, onDone, onLastDo, onBeforeDone, version)
    return this@Only
  }

  /** execute the onDo block only twice. */
  @JvmStatic
  inline fun onDoTwice(
    name: String,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit = {},
    version: String = ""
  ): Only {

    onDo(name, 2, onDo, onDone, version)
    return this@Only
  }

  /** execute the onDo block only twice with onLastDo and onBeforeDone. */
  @JvmStatic
  inline fun onDoTwice(
    name: String,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit = {},
    crossinline onLastDo: () -> Unit = {},
    crossinline onBeforeDone: () -> Unit = {},
    version: String = ""
  ): Only {

    onDo(name, 2, onDo, onDone, onLastDo, onBeforeDone, version)
    return this@Only
  }

  /** execute the onDo block only thrice. */
  @JvmStatic
  inline fun onDoThrice(
    name: String,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit = {},
    version: String = ""
  ): Only {

    onDo(name, 3, onDo, onDone, version)
    return this@Only
  }

  /** execute the onDo block only thrice with onLastDo and onBeforeDone. */
  @JvmStatic
  inline fun onDoThrice(
    name: String,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit = {},
    crossinline onLastDo: () -> Unit = {},
    crossinline onBeforeDone: () -> Unit = {},
    version: String = ""
  ): Only {

    onDo(name, 3, onDo, onDone, onLastDo, onBeforeDone, version)
    return this@Only
  }

  /** get Only time from the preference. */
  @JvmStatic
  fun getOnlyTimes(name: String): Int {
    return this.preference.getInt(name, 0)
  }

  /** set Only time from the preference. */
  @JvmStatic
  fun setOnlyTimes(name: String, time: Int) {
    this.preference.edit().putInt(name, time).apply()
  }

  /** get version data from the preference. */
  @JvmStatic
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  fun affectVersion(name: String, version: String): Boolean {
    val renderVersion = if (version.isEmpty()) buildVersion else version
    if (getOnlyVersion(name).equals(renderVersion)) {
      return false
    }
    setOnlyTimes(name, 0)
    setOnlyVersion(name, renderVersion)
    return true
  }

  /** get the Only version from the preference. */
  private fun getOnlyVersion(name: String): String? {
    return preference.getString(getOnlyVersionName(name), buildVersion)
      ?: return buildVersion
  }

  /** set the Only version from the preference. */
  private fun setOnlyVersion(name: String, version: String) {
    this.preference.edit().putString(getOnlyVersionName(name), version).apply()
  }

  /** get Only version preference naming convention. */
  private fun getOnlyVersionName(name: String): String {
    return name + "_version"
  }

  /** get Only executed or not about onBeforeDone. */
  @JvmStatic
  fun getOnBeforeDoneExecuted(name: String): Boolean {
    return this.preference.getBoolean(getOnBeforeDoneName(name), false)
  }

  /** set Only executed or not about onBeforeDone. */
  @JvmStatic
  @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
  fun setOnBeforeDoneExecuted(name: String) {
    this.preference.edit().putBoolean(getOnBeforeDoneName(name), true).apply()
  }

  /** get Only version preference naming convention. */
  private fun getOnBeforeDoneName(name: String): String {
    return name + "_onBeforeDone"
  }

  /** marks Only tag data. */
  @JvmStatic
  fun mark(name: String, marking: Any?) {
    marking?.let { this.preference.edit().putString(getMarkingName(name), it.toString()).apply() }
  }

  /** gets Only marking data. */
  @JvmStatic
  fun getMarking(name: String): String? {
    return this.preference.getString(getMarkingName(name), null)
  }

  /** get Only marking preference naming convention. */
  private fun getMarkingName(name: String): String {
    return name + "_marking"
  }

  /** remove a Only data from the preference. */
  @JvmStatic
  fun clearOnly(name: String) {
    with(this.preference.edit()) {
      remove(name).apply()
      remove(getOnlyVersion(name)).apply()
      remove(getOnBeforeDoneName(name)).apply()
      remove(getMarkingName(name)).apply()
    }
  }

  /** clear all Only data from the preference. */
  @JvmStatic
  fun clearAllOnly() {
    this.preference.edit().clear().apply()
  }

  /** Builder class for creating [Only]. */
  @OnlyDsl
  class Builder(
    val name: String,
    val times: Int = 1
  ) {

    @JvmField
    var onDo: () -> Unit = { }
    @JvmField
    var onDone: () -> Unit = { }
    @JvmField
    var onLastDo: () -> Unit = { }
    @JvmField
    var onBeforeDone: () -> Unit = { }
    @JvmField
    var version: String = ""
    @JvmField
    var marking: Any? = null

    /** executes the [onDo] block only as many times as necessary. */
    fun onDo(onDo: () -> Unit): Builder = apply { this.onDo = onDo }

    /** executes the [onDone] block after executing x+1 times [onDo] block. */
    fun onDone(onDone: () -> Unit): Builder = apply { this.onDone = onDone }

    /** executes only once the [onLastDo] block after finishing x times [onDo] block. */
    fun onLastDo(onLastDo: () -> Unit): Builder = apply { this.onLastDo = onLastDo }

    /** executes only once the [onBeforeDone] block after before executing [onDone] block. */
    fun onBeforeDone(onBeforeDone: () -> Unit): Builder = apply { this.onBeforeDone = onBeforeDone }

    /** changes version and clear the executed time history. */
    fun version(version: String): Builder = apply { this.version = version }

    /** marks to the [Only] data. */
    fun mark(marking: Any?): Builder = apply { this.marking = marking }

    fun run() = runByBuilder(this@Builder)
  }
}
