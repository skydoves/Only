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
import androidx.annotation.VisibleForTesting

@DslMarker
annotation class OnlyDsl

/** Run [Only] by [Only.Builder] using kotlin dsl. */
fun only(name: String, times: Int, block: Only.Builder.() -> Unit): Unit =
  Only.Builder(name, times).apply(block).run()

/** Run only once [Only] by [Only.Builder] using kotlin dsl. */
fun onlyOnce(name: String, times: Int = 1, block: Only.Builder.() -> Unit): Unit =
  Only.Builder(name, times).apply(block).run()

/** Run only twice [Only] by [Only.Builder] using kotlin dsl. */
fun onlyTwice(name: String, times: Int = 2, block: Only.Builder.() -> Unit): Unit =
  Only.Builder(name, times).apply(block).run()

/** Run only thrice [Only] by [Only.Builder] using kotlin dsl. */
fun onlyThrice(name: String, times: Int = 3, block: Only.Builder.() -> Unit): Unit =
  Only.Builder(name, times).apply(block).run()

/** Easy way to run block codes only as many times as necessary. */
@OnlyDsl
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

  /** check debugging mode. */
  fun onlyOnDoDebugMode(ignore: Boolean): Only {
    this.doOnDebugMode = ignore
    return this@Only
  }

  /** check debug mode. */
  fun isDebugMode(): Boolean {
    return doOnDebugMode && isDebuggable != 0
  }

  /** execute the onDo block only as many times as necessary. */
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
  inline fun onDo(
    name: String,
    times: Int,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit
  ): Only {

    // run only onDo block when debug mode.
    if (isDebugMode()) {
      onDo()
      return this@Only
    }

    val persistVersion = getOnlyTimes(name)
    if (persistVersion < times) {
      setOnlyTimes(name, persistVersion + 1)
      onDo()
    } else {
      onDone()
    }
    return this@Only
  }

  /**
   * execute the onDo block only as many times as necessary.
   * if the version is different from the old version, Only times will be initialized 0.
   */
  inline fun onDo(
    name: String,
    times: Int,
    crossinline onDo: () -> Unit,
    version: String = ""
  ): Only {

    affectVersion(name, version)
    onDo(name, times, onDo, { })

    return this@Only
  }

  /**
   * execute the onDo block only as many times as necessary.
   * if unnecessary, unCatch block will be executed.
   * if the version is different from the old version, Only times will be initialized 0.
   */
  inline fun onDo(
    name: String,
    times: Int,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit,
    version: String = ""
  ): Only {

    affectVersion(name, version)
    onDo(name, times, onDo, onDone)
    return this@Only
  }

  /** execute the onDo block only once. */
  inline fun onDoOnce(
    name: String,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit = {},
    version: String = ""
  ): Only {

    onDo(name, 1, onDo, onDone, version)
    return this@Only
  }

  /** execute the onDo block only twice. */
  inline fun onDoTwice(
    name: String,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit = {},
    version: String = ""
  ): Only {

    onDo(name, 2, onDo, onDone, version)
    return this@Only
  }

  /** execute the onDo block only thrice. */
  inline fun onDoThrice(
    name: String,
    crossinline onDo: () -> Unit,
    crossinline onDone: () -> Unit = {},
    version: String = ""
  ): Only {

    onDo(name, 3, onDo, onDone, version)
    return this@Only
  }

  /** get Only time from the preference. */
  fun getOnlyTimes(name: String): Int {
    return this.preference.getInt(name, 0)
  }

  /** set Only time from the preference. */
  fun setOnlyTimes(name: String, time: Int) {
    this.preference.edit().putInt(name, time).apply()
  }

  /** get version data from the preference. */
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

  /** remove a Only data from the preference. */
  fun clearOnly(name: String) {
    this.preference.edit().remove(name).apply()
  }

  /** clear all Only data from the preference. */
  fun clearAllOnly() {
    this.preference.edit().clear().apply()
  }

  /** Builder class for creating [Only]. */
  class Builder(
    private val name: String,
    private val times: Int = 1
  ) {

    @JvmField
    var onDo: () -> Unit = { }
    @JvmField
    var onDone: () -> Unit = { }
    @JvmField
    var version: String = ""

    fun onDo(onDo: () -> Unit): Builder = apply { this.onDo = onDo }
    fun onDone(onDone: () -> Unit): Builder = apply { this.onDone = onDone }
    fun version(version: String): Builder = apply { this.version = version }

    fun run() {
      onDo(name, times, onDo, onDone, version)
    }
  }
}
