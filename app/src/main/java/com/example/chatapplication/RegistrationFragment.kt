package com.example.chatapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chatapplication.databinding.FragmentRegistrationBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File


class RegistrationFragment : Fragment() {

    private lateinit var binding: FragmentRegistrationBinding
    private lateinit var auth: FirebaseAuth
    private var uri : Uri? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private lateinit var dataRef: DatabaseReference
    private lateinit var storageRef: StorageReference
    private lateinit var imageURL: String
    private val status = "Hey there I'm using this Application"


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentRegistrationBinding.inflate(inflater)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        binding.signIn.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent?  = result.data
                if(data!=null) {
                    uri = data.data!!
                    binding.profileImage.setImageURI(uri)
                }

            }

        }

        fun galleryCall() {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            launcher.launch(Intent.createChooser(intent, "Choose Image"))
        }

        binding.profileImage.setOnClickListener {
            galleryCall()
        }

        binding.signUp.setOnClickListener {

            val email = binding.RegE.editText?.text.toString()
            val password = binding.RegP.editText?.text.toString()
            val cnfPassword = binding.RegCNFP.editText?.text.toString()
            val name = binding.RegName.editText?.text.toString()

                if(email=="" && name =="") {
                    binding.RegE.editText?.error="Enter Email"
                    binding.RegName.error="Enter Name"
                }
            else if(email=="") {
                    binding.RegE.editText?.error="Enter Email"
                }
            else if(name=="") {
                    binding.RegName.error="Enter Name"
                }
            else if(password=="") {
                Toast.makeText(this.requireActivity(), "Enter Password", Toast.LENGTH_SHORT).show()
                }
            else if(cnfPassword=="") {
                    Toast.makeText(this.requireActivity(), "Confirm Your Password", Toast.LENGTH_SHORT).show()
                }
            else {
                    binding.PB.visibility = View.VISIBLE
                    if(password == cnfPassword) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this.requireActivity()) { task ->
                                if (task.isSuccessful) {
                                    dataRef = database.reference.child("user").child(auth.uid!!)
                                    storageRef = storage.reference.child("upload").child(auth.uid!!)
                                    if(uri!=null) {
                                       storageRef.putFile(uri!!).addOnCompleteListener(object : OnCompleteListener<UploadTask.TaskSnapshot> {
                                            override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {
                                                if(task.isSuccessful()) {
                                                    storageRef.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                                                        override fun onSuccess(URI: Uri?) {
                                                           imageURL = URI.toString()
                                                            val data = DataHolder(auth.uid!!, name, email, status, imageURL)
                                                            dataRef.setValue(data).addOnCompleteListener(object: OnCompleteListener<Void> {
                                                                override fun onComplete(task: Task<Void>) {
                                                                   if(task.isSuccessful) {
                                                                       binding.PB.visibility = View.INVISIBLE
                                                                       Toast.makeText(requireContext(), "Sign up Successful", Toast.LENGTH_SHORT).show()
                                                                       findNavController().navigate(R.id.action_registrationFragment_to_mainScreenFragment)
                                                                   }
                                                                }

                                                            })

                                                        }

                                                    })
                                                }
                                            }

                                        }) as UploadTask

//                                        storageRef.downloadUrl.addOnSuccessListener {
//                                            OnSuccessListener<Uri> { URI -> imageURL = URI.toString() }
//                                        }
                                    }
//                                    val data = DataHolder(auth.uid!!, name, email, status, imageURL)
//                                    dataRef.setValue(data)
//                                    findNavController().navigate(R.id.action_registrationFragment2_to_mainScreenFragment)

                                } else {
                                    binding.PB.visibility = View.INVISIBLE
                                    Toast.makeText(this.requireContext(), "Sign up Failed", Toast.LENGTH_SHORT).show()
                                }
                            }
                    }
                    else {
                        binding.PB.visibility = View.INVISIBLE
                        Toast.makeText(this.requireContext(), "Passwords do not Match", Toast.LENGTH_SHORT).show()
                    }

                }



        }
    }


}