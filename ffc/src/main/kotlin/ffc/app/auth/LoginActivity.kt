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

package ffc.app.auth

import android.os.Bundle
import android.transition.Explode
import android.transition.Slide
import android.util.Log
import android.view.Gravity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import ffc.android.allowTransitionOverlap
import ffc.android.enterDuration
import ffc.android.exit
import ffc.android.find
import ffc.android.findFirst
import ffc.android.setTransition
import ffc.android.tag
import ffc.app.BuildConfig
import ffc.app.FamilyFolderActivity
import ffc.app.MainActivity
import ffc.app.R
import ffc.app.auth.fragment.LoginExceptionPresenter
import ffc.app.auth.fragment.LoginOrgFragment
import ffc.app.auth.fragment.LoginUserFragment
import ffc.entity.Organization
import ffc.entity.gson.parseTo
import ffc.entity.gson.toJson
import jp.wasabeef.glide.transformations.BlurTransformation
import kotlinx.android.synthetic.main.activity_login.*
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.toast

class LoginActivity : FamilyFolderActivity(), LoginPresenter {

    private lateinit var interactor: LoginInteractor

    val isRelogin
        get() = intent.getBooleanExtra("relogin", false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        initInstances()

        val auth = auth(this)
        if (auth.user != null) {
            toast("Hello ${auth.user?.name}")
            Log.d(tag, "User id = ${auth.user?.id}")
        }
        interactor = LoginInteractor(this, auth, isRelogin)

        savedInstanceState?.let { saved ->
            val org = saved.getString("org")?.parseTo<Organization>()
            org?.let { interactor.org = org }
        }
        versionView.text = "V ${BuildConfig.VERSION_NAME}"
    }

    private fun initInstances() {
        blurImage()
    }

    private fun blurImage() {
        Glide.with(this)
            .load(R.drawable.community)
            .apply(RequestOptions.bitmapTransform(BlurTransformation(4, 3)))
            .into(ivCommunity)
    }

    override fun showOrgSelector() {
        if (savedInstanceState == null) {
            val fragment = LoginOrgFragment()
            fragment.orgSelected = { interactor.org = it }
            fragment.setTransition {
                exitTransition = Explode().exit()
                allowTransitionOverlap = false
            }
            supportFragmentManager.beginTransaction()
                .replace(R.id.contentContainer, fragment, "Org")
                .commit()
        }
    }

    override fun onLoginSuccess() {
        if (!isRelogin) {
            startActivity<MainActivity>()
        }
        finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        interactor.org?.let { outState.putString("org", it.toJson()) }
        super.onSaveInstanceState(outState)
    }

    override fun onError(throwable: Throwable) {
        supportFragmentManager
            .findFirst<LoginExceptionPresenter>("Login", "Org")?.onException(throwable)
    }

    override fun onOrgSelected(org: Organization) {
        val userPassFragment = supportFragmentManager.find("Login")
            ?: LoginUserFragment()
        userPassFragment.onLogin = { user, pass -> interactor.doLogin(user, pass) }
        userPassFragment.org = org
        val orgFragment = supportFragmentManager.find<LoginOrgFragment>("Org")
        userPassFragment.setTransition {
            enterTransition = Slide(Gravity.END).apply {
                startDelay = 50
                duration = enterDuration
            }
        }

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.contentContainer, userPassFragment, "Login")
                .hide(orgFragment)
                .addToBackStack(null)
                .commit()
        }
    }
}

fun FamilyFolderActivity.relogin() {
    startActivity<LoginActivity>("relogin" to true)
}
