package com.example.chatapplication

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.chatapplication.databinding.FragmentLoginBinding
import com.google.firebase.auth.FirebaseAuth

class LoginFragment : Fragment() {

    private lateinit var binding : FragmentLoginBinding
    private lateinit var auth : FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentLoginBinding.inflate(inflater)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()


        binding.signUp.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registrationFragment)
        }
        binding.LogButton.setOnClickListener {

            val email = binding.LogE.editText?.text.toString()
            val password = binding.LogP.editText?.text.toString()
            if(email == "" && password =="") {
                binding.LogE.editText?.error = "Enter Email"
            }
            else if(password=="") {
              Toast.makeText(this.requireActivity(), "Enter Password", Toast.LENGTH_SHORT).show()
            }
            else if (email=="") {
                binding.LogE.editText?.error = "Enter Email"
            }
            else {
                binding.pB.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this.requireActivity()) { task ->
                        if (task.isSuccessful) {
                            binding.pB.visibility=View.INVISIBLE
                            findNavController().navigate(R.id.action_loginFragment_to_mainScreenFragment)


                        } else {
                            binding.pB.visibility=View.INVISIBLE
                            Toast.makeText(this.requireContext(), "Incorrect Email or Password", Toast.LENGTH_SHORT).show()
                        }
                    }
            }

        }
    }
}