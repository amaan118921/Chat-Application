package com.example.chatapplication

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.chatapplication.databinding.FragmentStartingScreenBinding

class StartingScreenFragment : Fragment() {

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var binding: FragmentStartingScreenBinding
    }



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentStartingScreenBinding.inflate(inflater)
        binding = fragmentBinding
        return fragmentBinding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       Handler(Looper.getMainLooper()).postDelayed({
            findNavController().navigate(R.id.action_startingScreenFragment_to_mainScreenFragment)
       }, 1500)
    }

}