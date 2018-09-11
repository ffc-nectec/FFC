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

package ffc.v3.authen.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ffc.entity.Organization
import ffc.entity.gson.parseTo
import ffc.v3.BuildConfig
import ffc.v3.R
import ffc.v3.authen.LoginInteractor
import ffc.v3.authen.LoginPresenter
import ffc.v3.authen.exception.LoginErrorException
import ffc.v3.authen.exception.LoginFailureException
import ffc.v3.authen.getIdentityRepo
import ffc.v3.util.LoginEventListener
import ffc.v3.util.assertionNotEmpty
import kotlinx.android.synthetic.main.fragment_login_user.btnBack
import kotlinx.android.synthetic.main.fragment_login_user.btnLogin
import kotlinx.android.synthetic.main.fragment_login_user.etPwd
import kotlinx.android.synthetic.main.fragment_login_user.etUsername
import kotlinx.android.synthetic.main.fragment_login_user.inputLayoutPassword
import kotlinx.android.synthetic.main.fragment_login_user.inputLayoutUsername
import kotlinx.android.synthetic.main.fragment_login_user.tvHospitalName
import org.jetbrains.anko.support.v4.longToast

class LoginUserFragment : Fragment(), View.OnClickListener, LoginPresenter {

    private lateinit var orgIdBundle: Bundle
    lateinit var org: Organization
    lateinit var organization: Organization
    private lateinit var interactor: LoginInteractor
    private lateinit var loginEventListener: LoginEventListener

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login_user, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        orgIdBundle = arguments!!
        org = orgIdBundle.getString("organization")!!.parseTo()

        loginEventListener = activity as LoginEventListener

        btnLogin.setOnClickListener(this)
        btnBack.setOnClickListener(this)

        tvHospitalName.text = org.name

        interactor = LoginInteractor()
        interactor.loginPresenter = this
        interactor.idRepo = getIdentityRepo(context!!)

        // Debug User Login
        if (BuildConfig.DEBUG) {
            btnLogin.setOnLongClickListener {
                etUsername.setText("ploy")
                etPwd.setText("n")
                true
            }
        }
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.btnLogin -> {
                // Assert username and password
                val username = etUsername.text.toString()
                val password = etPwd.text.toString()
                val checkUsername = assertionNotEmpty(inputLayoutUsername, username,
                    getString(R.string.no_username))
                val checkPwd = assertionNotEmpty(inputLayoutPassword, password,
                    getString(R.string.no_password))

                // Login
                if (checkUsername && checkPwd) {
                    loginEventListener.onShowProgressBar(true)
                    interactor.org = org
                    interactor.doLogin(username, password)
                }
            }

            R.id.btnBack ->
                fragmentManager!!.popBackStack()
        }
    }

    override fun onLoginSuccess() {
        // Start MapActivity
        loginEventListener.onShowProgressBar(false)
        loginEventListener.onLoginActivity()
    }

    override fun onError(throwable: Throwable) {
        loginEventListener.onShowProgressBar(false)
        val message = when (throwable) {
            is LoginErrorException -> getString(R.string.identification_error)
            is LoginFailureException -> throwable.message
            else -> "Something wrong"
        }
        message?.let { longToast(it) }
    }
}
