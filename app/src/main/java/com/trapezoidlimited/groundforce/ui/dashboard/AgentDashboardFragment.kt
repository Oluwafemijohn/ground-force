package com.trapezoidlimited.groundforce.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.navigation.fragment.findNavController
import com.trapezoidlimited.groundforce.R
import com.trapezoidlimited.groundforce.databinding.FragmentAgentDashboardBinding
import com.trapezoidlimited.groundforce.utils.DataListener

class AgentDashboardFragment : Fragment() {

    private var _binding: FragmentAgentDashboardBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentAgentDashboardBinding.inflate(inflater, container, false)

        /** setting toolbar text **/
        binding.dashboardToolBarLy.toolbarTitle.text = getString(R.string.home_title_str)

        // Inflate the layout for this fragment
        return binding.root
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        binding.fragmentAgentDashboardMissionsButtonIb.setOnClickListener {
            findNavController().navigate(R.id.action_agentDashboardFragment_to_tasksFragment)
        }

        binding.fragmentAgentDashboardSurveysButtonIb.setOnClickListener {
            findNavController().navigate(R.id.action_agentDashboardFragment_to_surveyListFragment2)
        }

        /** setting mutableLiveData value controlling where the tab will start on the mission fragment  */

        binding.fragmentAgentDashboardActiveButtonIb.setOnClickListener {

            DataListener.mSetStartTab.value = true
            findNavController().navigate(R.id.action_agentDashboardFragment_to_tasksFragment)
        }

        binding.fragmentAgentDashboardSeeDetailsButton.setOnClickListener {
            findNavController().navigate(R.id.action_agentDashboardFragment_to_paymentHistory)
        }

        binding.fragmentAgentViewDetailsTv.setOnClickListener {
            findNavController().navigate(R.id.action_agentDashboardFragment_to_historyFragment)
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}