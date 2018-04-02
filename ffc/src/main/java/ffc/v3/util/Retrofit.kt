/*
 * Copyright (c) 2018 NECTEC
 *   National Electronics and Computer Technology Center, Thailand
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ffc.v3.util

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

inline fun <reified T> Call<T>.then(crossinline task: (Response<T>?, Throwable?) -> Unit) {
  enqueue(object : Callback<T> {
    override fun onResponse(call: Call<T>, response: Response<T>) {
      task(response, null)
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
      task(null, t)
    }
  })
}
