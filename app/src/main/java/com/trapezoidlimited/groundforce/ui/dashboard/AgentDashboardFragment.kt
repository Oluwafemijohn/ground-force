package com.trapezoidlimited.groundforce.ui.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.trapezoidlimited.groundforce.R
import com.trapezoidlimited.groundforce.api.LoginAuthApi
import com.trapezoidlimited.groundforce.api.Resource
import com.trapezoidlimited.groundforce.databinding.FragmentAgentDashboardBinding
import com.trapezoidlimited.groundforce.repository.AuthRepositoryImpl
import com.trapezoidlimited.groundforce.utils.*
import com.trapezoidlimited.groundforce.viewmodel.LoginAuthViewModel
import com.trapezoidlimited.groundforce.viewmodel.ViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_dashboard.*
import retrofit2.Retrofit
import javax.inject.Inject

@AndroidEntryPoint
class AgentDashboardFragment : Fragment() {

    @Inject
    lateinit var loginApiService: LoginAuthApi

    @Inject
    lateinit var retrofit: Retrofit

    private var _binding: FragmentAgentDashboardBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: LoginAuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (_binding == null) {
            _binding = FragmentAgentDashboardBinding.inflate(inflater, container, false)
            /** setting toolbar text **/
            binding.dashboardToolBarLy.toolbarTitle.text = getString(R.string.home_title_str)
        }

        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        val repository = AuthRepositoryImpl(loginApiService)
        val factory = ViewModelFactory(repository)

        viewModel = ViewModelProvider(this, factory).get(LoginAuthViewModel::class.java)


        viewModel.getUserResponse.observe(viewLifecycleOwner, {

            when (it) {
                is Resource.Success -> {

                    Log.i("USER", it.value.data?.firstName!!)
                    Log.i("USER", it.value.data?.lastName!!)
                    Log.i("USER", it.value.data?.email!!)

                    Toast.makeText(requireContext(), it.value.data?.firstName!!, Toast.LENGTH_SHORT).show()

                }
                is Resource.Failure -> {
                    handleApiError(it, retrofit, requireView())
                }
            }
        })



        binding.fragmentAgentDashboardMissionsButtonIb.setOnClickListener {
            DataListener.currentItem = MISSION
            findNavController().navigate(R.id.action_agentDashboardFragment_to_tasksFragment)
        }

        binding.fragmentAgentDashboardSurveysButtonIb.setOnClickListener {
            findNavController().navigate(R.id.action_agentDashboardFragment_to_surveyListFragment2)
        }

        /** setting mutableLiveData value controlling where the tab will start on the mission fragment  */

        binding.fragmentAgentDashboardActiveButtonIb.setOnClickListener {
            DataListener.currentItem = ONGOING
            findNavController().navigate(R.id.action_agentDashboardFragment_to_tasksFragment)
        }

        binding.fragmentAgentDashboardSeeDetailsButton.setOnClickListener {
            findNavController().navigate(R.id.action_agentDashboardFragment_to_paymentHistory)
        }

        binding.fragmentAgentViewDetailsTv.setOnClickListener {
            // Setting currentItem value for mission-survey-history viewpager
            DataListener.msCurrentItem = MISSIONCOMPLETED
            findNavController().navigate(R.id.action_agentDashboardFragment_to_historyFragment)
        }

        binding.fragmentAgentSurveyViewDetailsTv.setOnClickListener {
            // Setting currentItem value for mission-survey-history viewpager
            DataListener.msCurrentItem = SURVEYCOMPLETED
            findNavController().navigate(R.id.action_agentDashboardFragment_to_historyFragment)
        }


        binding.fragmentAgentDashboardCloseIconIv.setOnClickListener {
            binding.fragmentAgentDashboardIncompleteProfileCl.visibility = View.GONE

            val userId = loadFromSharedPreference(requireActivity(), USERID)

            viewModel.getUser(userId)
        }


        // Navigate to Home on Back Press
        requireActivity().onBackPressedDispatcher.addCallback {
            if (findNavController().currentDestination?.id == R.id.agentDashboardFragment) {
                activity?.finish()
            } else {
                findNavController().popBackStack()
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}