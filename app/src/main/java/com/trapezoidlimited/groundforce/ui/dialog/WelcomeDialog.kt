package com.trapezoidlimited.groundforce.ui.dialog

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.trapezoidlimited.groundforce.EntryApplication
import com.trapezoidlimited.groundforce.R
import com.trapezoidlimited.groundforce.api.LoginAuthApi
import com.trapezoidlimited.groundforce.api.Resource
import com.trapezoidlimited.groundforce.databinding.WelcomeDialogBinding
import com.trapezoidlimited.groundforce.repository.AuthRepositoryImpl
import com.trapezoidlimited.groundforce.room.RoomAgent
import com.trapezoidlimited.groundforce.ui.dashboard.DashboardActivity
import com.trapezoidlimited.groundforce.utils.*
import com.trapezoidlimited.groundforce.viewmodel.AuthViewModel
import com.trapezoidlimited.groundforce.viewmodel.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Retrofit
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeDialog : DialogFragment() {

    @Inject
    lateinit var loginApiService: LoginAuthApi

    @Inject
    lateinit var retrofit: Retrofit

    private var _binding: WelcomeDialogBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel
    private lateinit var progressBar : ProgressBar
    private lateinit var oKTextView: TextView
    private val roomViewModel by lazy { EntryApplication.viewModel(this) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog!!.window?.setBackgroundDrawableResource(R.drawable.round_corner)
        _binding = WelcomeDialogBinding.inflate(layoutInflater, container, false)



        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        progressBar = binding.welcomeDialogPb
        oKTextView = binding.welcomeDialogOkTv

        val repository = AuthRepositoryImpl(loginApiService)
        val factory = ViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(AuthViewModel::class.java)


        viewModel.getUserResponse.observe(viewLifecycleOwner, {

            when (it) {
                is Resource.Success -> {

                    /** Adding Agent Details to Room Database */

                    Toast.makeText(requireContext(), it.value.data?.firstName!!, Toast.LENGTH_SHORT).show()

                    val roomAgent = RoomAgent(
                        agentId = 1,
                        id = it.value.data?.id!!,
                        lastName = it.value.data.lastName,
                        firstName = it.value.data.firstName,
                        email = it.value.data.email,
                        residentialAddress = it.value.data.residentialAddress,
                        dob = it.value.data.dob,
                        gender = it.value.data.gender,
                    )

                    roomViewModel.addAgent(roomAgent)

                    setInVisibility(progressBar)
                    goToDashboard()

                }
                is Resource.Failure -> {
                    setInVisibility(progressBar)
                    setVisibility(oKTextView)
                    handleApiError(it, retrofit, requireView())
                }
            }
        })


        binding.welcomeDialogOkTv.setOnClickListener {

            val userId = loadFromSharedPreference(requireActivity(), USERID)

            setInVisibility(oKTextView)
            setVisibility(progressBar)
            viewModel.getUser(userId)
        }


    }

    override fun onStart() {
        super.onStart()
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.25).toInt()
        dialog!!.window?.setLayout(width, height)
    }

    private fun goToDashboard() {
        Intent(requireContext(), DashboardActivity::class.java).also { intent ->
            startActivity(intent)
            requireActivity().finish()
        }
        requireActivity().finish()
    }
}