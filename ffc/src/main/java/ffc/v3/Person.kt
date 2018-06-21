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

package ffc.v3

import com.google.gson.annotations.SerializedName
import me.piruin.geok.LatLng
import me.piruin.geok.geometry.Point
import org.joda.time.LocalDate
import java.util.UUID

data class Person(@SerializedName("_id") val id: String = UUID.randomUUID().toString()) {
  var hospCode: String? = null
  var pid: Long? = null
  var prename: String = ""
  var firstname: String = ""
  var lastname: String = ""
  val name: String
    get() = "$prename$firstname $lastname"

  var birthData: LocalDate? = null
  var identities: MutableList<Identity> = mutableListOf()
  var house: Address? = null
  var chronics: MutableList<Chronic> = mutableListOf()
}

data class Address(@SerializedName("_id") val id: String = UUID.randomUUID().toString()) {
  var identity: Identity? = null
  var type: Type = Type.House
  var no: String? = null
  var road: String? = null
  var tambon: String? = null
  var ampur: String? = null
  var changwat: String? = null
  var haveChronics: Boolean = false
  var people: List<Person> = mutableListOf()
  var location: Point? = null
  @Deprecated(
    "Use location (Point) instead",
    ReplaceWith("location", "me.piruin.geok.geometry.Point"))
  var coordinates: LatLng? = null

  enum class Type {
    House,
    Condo
  }
}

data class Chronic(
  val idc10: String,
  val diagDate: LocalDate
) {
  var diagHospCode: String? = null
  var careHospCode: String? = null
  var status: String = "active"
  var dischardDate: LocalDate? = null
}

interface Identity {
  val id: String
  val type: String
  fun isValid(): Boolean
}

data class ThaiCitizenId(override val id: String) : Identity {
  override val type: String = "thailand-citizen-id"

  override fun isValid(): Boolean = id.length == 13
}

data class ThaiHouseholdId(override val id: String) : Identity {
  override val type: String = "thailand-household-id"

  override fun isValid(): Boolean = id.length == 11
}



