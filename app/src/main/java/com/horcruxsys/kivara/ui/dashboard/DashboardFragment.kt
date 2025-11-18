package com.horcruxsys.kivara.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.horcruxsys.kivara.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null

    // This property is only valid between onCreateView and onDestroyView
    private val binding: FragmentDashboardBinding
        get() = checkNotNull(_binding) {
            "Fragment $this binding cannot be accessed before onCreateView() or after onDestroyView()"
        }
    
    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        dashboardViewModel.text.observe(viewLifecycleOwner) { text ->
            binding.textDashboard.text = text
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
