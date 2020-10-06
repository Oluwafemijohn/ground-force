package com.trapezoidlimited.groundforce.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.trapezoidlimited.groundforce.adapters.OnBoardingViewPagerAdapter
import com.trapezoidlimited.groundforce.databinding.FragmentOnBoardingBinding

class OnBoardingFragment : Fragment() {

    private lateinit var binding: FragmentOnBoardingBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentOnBoardingBinding.inflate(inflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Set View PagerAdapter
        val fragmentList: ArrayList<Fragment> = arrayListOf(
            SecondScreen(),
            SecondScreen(),
            SecondScreen()
        )

        //Connect the fragment list to the view pager
        val adapter = activity?.supportFragmentManager?.let {
            OnBoardingViewPagerAdapter(
                fragmentList,
                it,
                lifecycle
            )
        }
        binding.fragmentOnBoardingVp.adapter = adapter


        //Ue TabLayoutMediator to set indicator to the tab layout
        TabLayoutMediator(
            binding.fragmentOnBoardingIndicatorTl,
            binding.fragmentOnBoardingVp
        ) { tab, position ->
        }.attach()
    }

}