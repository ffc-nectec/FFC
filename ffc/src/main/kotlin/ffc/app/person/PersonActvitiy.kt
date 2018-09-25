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

package ffc.app.person

import android.os.Bundle
import ffc.android.onClick
import ffc.app.FamilyFolderActivity
import ffc.app.R
import ffc.app.healthservice.HealthCareServicesFragment
import ffc.app.healthservice.HomeVisitActivity
import ffc.app.isDev
import ffc.entity.Person
import kotlinx.android.synthetic.main.activity_person.ageView
import kotlinx.android.synthetic.main.activity_person.nameView
import kotlinx.android.synthetic.main.activity_person.visitButton
import org.jetbrains.anko.bundleOf
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class PersonActvitiy : FamilyFolderActivity() {

    private var personId
        get() = intent.getStringExtra("personId")
        set(value) {
            intent.putExtra("personId", value)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)

        if (isDev && personId == null) {
            personId = mockPerson.id
        }

        if (savedInstanceState == null) {
            val fragment = HealthCareServicesFragment()
            fragment.arguments = bundleOf("personId" to personId)
            supportFragmentManager
                .beginTransaction()
                .add(R.id.contentContainer, fragment)
                .commit()
        }

        visitButton.onClick {
            startActivity<HomeVisitActivity>("personId" to personId)
        }
    }

    override fun onResume() {
        super.onResume()

        persons().person(personId) {
            onFound { bind(it) }
            onNotFound { toast("Not found") }
        }
    }

    private fun bind(person: Person) {
        with(person) {
            nameView.text = name
            age?.let { ageView.text = "อายุ $it ปี" }

        }
    }
}
