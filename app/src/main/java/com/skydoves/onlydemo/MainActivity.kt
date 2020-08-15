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

package com.skydoves.onlydemo

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.skydoves.only.Only
import com.skydoves.only.only
import com.skydoves.only.onlyOnce
import com.skydoves.only.onlyVisibility
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

  private val tag = javaClass.simpleName

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)

    /**
     * initialize Only.
     * This initialization can be called only once in Application class.
     */
    Only.init(this)

    onlyOnce("background") {
      onDo { parentLayout.setBackgroundColor(Color.GRAY) }
    }

    only("Intro", times = 3) {
      onDo { times ->
        Toast.makeText(baseContext, "shows: $times", Toast.LENGTH_SHORT).show()
        Log.d(tag, "onDo: $times")
      }
      onLastDo {
        Toast.makeText(baseContext, "it was the last shows", Toast.LENGTH_SHORT).show()
        Log.d(tag, "onLastDo")
      }
      onBeforeDone {
        Toast.makeText(baseContext, "starts done", Toast.LENGTH_SHORT).show()
        Log.d(tag, "onBeforeDone")
      }
      onDone {
        Toast.makeText(baseContext, "done", Toast.LENGTH_SHORT).show()
        Log.d(tag, "onDone")
      }
      version = BuildConfig.VERSION_NAME
    }

    button.onlyVisibility(name = "button", times = 1, visible = true)
  }
}
