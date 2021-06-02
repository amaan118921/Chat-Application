package com.example.chatapplication

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.chatapplication.databinding.FragmentSettingsBinding
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso

class SettingsFragment : Fragment() {

    private lateinit var binding : FragmentSettingsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var storage : FirebaseStorage
    private lateinit var reference: DatabaseReference
    private lateinit var imgURI: String
    private lateinit var userName: String
    private lateinit var userStatus: String
    private lateinit var storageReference: StorageReference
    private lateinit var tempRef: DatabaseReference
    private var uri: Uri? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentSettingsBinding.inflate(inflater)
        binding = fragmentBinding
        return fragmentBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()
        tempRef = database.reference.child(auth.uid!!)

        val prevName = binding.name.editText?.text
        val prevStatus = binding.status.editText?.text
        val userImage = binding.imageSetting

        reference = database.reference.child("user").child(auth.uid!!)

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                imgURI = snapshot.child("imageUri").value.toString()
                Picasso.get().load(imgURI).into(userImage)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })


       reference.addValueEventListener(object : ValueEventListener {
           override fun onDataChange(snapshot: DataSnapshot) {
               userName = snapshot.child("name").value.toString()
               binding.name.editText?.setText(userName)
           }

           override fun onCancelled(error: DatabaseError) {
               TODO("Not yet implemented")
           }

       })

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userStatus = snapshot.child("status").value.toString()
                binding.status.editText?.setText(userStatus)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent?  = result.data
                if(data!=null) {
                    uri = data.data!!
                    binding.imageSetting.setImageURI(uri)
                }

            }

        }

        fun galleryCall() {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            launcher.launch(Intent.createChooser(intent, "Choose Image"))
        }

        storageReference = storage.reference.child("upload").child(auth.uid!!)

        binding.MCV.setOnClickListener{
            galleryCall()
        }

        binding.save.setOnClickListener {


            val userStatus = binding.status.editText?.text
            if(userStatus!=prevStatus) {
                binding.PB.visibility = View.VISIBLE
                reference.child("status").setValue(userStatus.toString()).addOnCompleteListener(object: OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        if(p0.isSuccessful) {
                            binding.PB.visibility = View.INVISIBLE
                        }
                    }

                })



            }
            val userName = binding.name.editText?.text
            if(userName!=prevName) {
                binding.PB.visibility = View.VISIBLE
                reference.child("name").setValue(userName.toString()).addOnCompleteListener(object : OnCompleteListener<Void> {
                    override fun onComplete(p0: Task<Void>) {
                        if(p0.isSuccessful) {
                            binding.PB.visibility = View.INVISIBLE
                        }
                    }

                })
            }

            if(uri!=null) {
                binding.PB.visibility = View.VISIBLE
                storageReference.putFile(uri!!).addOnCompleteListener(object: OnCompleteListener<UploadTask.TaskSnapshot> {
                    override fun onComplete(task: Task<UploadTask.TaskSnapshot>) {
                        if(task.isSuccessful()) {
                            storageReference.downloadUrl.addOnSuccessListener(object : OnSuccessListener<Uri> {
                                override fun onSuccess(URI: Uri?) {
                                    reference.child("imageUri").setValue(URI.toString()).addOnCompleteListener(object: OnCompleteListener<Void> {
                                        override fun onComplete(task: Task<Void>) {
                                            if(task.isSuccessful) {
                                                binding.PB.visibility = View.INVISIBLE
                                                findNavController().navigate(R.id.action_settingsFragment_to_mainScreenFragment)
                                            }
                                        }

                                    })
                                }

                            })
                        }
                    }

                })
            }
            Toast.makeText(requireActivity(), "Changes Saved", Toast.LENGTH_SHORT).show()

            findNavController().navigate(R.id.action_settingsFragment_to_mainScreenFragment)

        }


    }


}