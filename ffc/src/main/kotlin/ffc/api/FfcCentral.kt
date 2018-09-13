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

package ffc.api

import ffc.entity.gson.ffcGson
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit.SECONDS

var url_ploy = "https://ffc-test.herokuapp.com/v0/"
var url_max = "https://ffc-nectec.herokuapp.com/v0/"
var url_old = "https://ffc-api-test.herokuapp.com/v0/"

class FfcCentral(url: String = url_old) {

    val retrofitBuilder = Retrofit.Builder().baseUrl(url)!!

    inline fun <reified T> service(): T {
        val httpBuilder: OkHttpClient.Builder = OkHttpClient.Builder().apply {
            readTimeout(60, SECONDS)
            writeTimeout(60, SECONDS)
            connectTimeout(30, SECONDS)
            cache?.let { cache(it) }
            addInterceptor(DefaultInterceptor())
            TOKEN?.let { addInterceptor(AuthTokenInterceptor(it)) }
        }

        return retrofitBuilder
            .addConverterFactory(GsonConverterFactory.create(ffcGson))
            .client(httpBuilder.build())
            .build()
            .create(T::class.java)
    }

    companion object {
        var TOKEN: String? = null
        var cache: Cache? = null
    }
}