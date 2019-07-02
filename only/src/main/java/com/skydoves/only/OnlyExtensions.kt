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
import android.view.View
import android.widget.Toast

/** View visibility [Only] extension.  */
fun View.onlyVisibility(name: String, times: Int, visible: Boolean) {
  val view = this
  only(name, times) {
    onDo { view.visible(visible) }
    onDone { view.visible(!visible) }
  }
}

internal fun View.visible(visible: Boolean) {
  if (visible) this.visibility = View.VISIBLE
  else this.visibility = View.GONE
}

/** shows toast only x times. */
fun Context.onlyToast(name: String, times: Int, text: String) {
  only(name, times) {
    onDo { Toast.makeText(this@onlyToast, text, Toast.LENGTH_SHORT).show() }
  }
}

/** shows toast only once. */
fun Context.onlyOnceToast(name: String, text: String) {
  onlyToast(name, 1, text)
}

/** shows toast only twice. */
fun Context.onlyTwiceToast(name: String, text: String) {
  onlyToast(name, 2, text)
}

/** shows toast only thrice */
fun Context.onlyThriceToast(name: String, text: String) {
  onlyToast(name, 3, text)
}
