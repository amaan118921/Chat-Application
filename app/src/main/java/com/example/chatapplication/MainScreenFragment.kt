package com.example.chatapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStateManagerControl
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chatapplication.databinding.FragmentMainScreenBinding
import com.example.chatapplication.viewModel.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainScreenFragment : Fragment() {
    private lateinit var binding : FragmentMainScreenBinding
    private lateinit var auth : FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var reference : DatabaseReference
    private lateinit var tempReference: DatabaseReference
    private var temp = 0




    private val Model: ChatViewModel by activityViewModels()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val fragmentBinding = FragmentMainScreenBinding.inflate(inflater)
        binding = fragmentBinding
        return fragmentBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val fragment : Fragment? = parentFragmentManager.findFragmentById(R.id.startingScreenFragment)
        if(fragment!=null) {
            parentFragmentManager.beginTransaction().remove(fragment).commit()
        }
        val userData: MutableList<DataHolder> = mutableListOf()
        val tempData: MutableList<DataHolder> = mutableListOf()
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        reference = database.reference.child("user")



        if(auth.currentUser==null) {
            findNavController().navigate(R.id.action_mainScreenFragment_to_loginFragment)

        }
        else {
            tempReference = database.reference.child(auth.uid!!)


            tempReference.addValueEventListener( object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        userData.clear()
                        for(dataSnapshot: DataSnapshot in snapshot.children) {
                            val data: DataHolder? = dataSnapshot.getValue(DataHolder::class.java)
                            userData.add(data!!)
                        }
                        binding.homeRecyclerView.adapter?.notifyDataSetChanged()

                    }
                        override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_SHORT).show()
                    }
                })
            }

        reference.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                for(dataSnapshot: DataSnapshot in snapshot.children) {
                    val data: DataHolder? = dataSnapshot.getValue(DataHolder::class.java)
                    for(d in userData) {
                        if(d.uid == data?.uid){
                           tempReference.child(data.uid).setValue(data)
                            userData.remove(d)
                            userData.add(data)
                        }
                    }

                }
                binding.homeRecyclerView.adapter?.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })






        binding.offButton.setOnClickListener{
            auth.signOut()
            findNavController().navigate(R.id.action_mainScreenFragment_to_loginFragment)
//                    val dialog = Dialog(requireActivity(), R.style.Dialogue)
//        dialog.setContentView(R.layout.dialog_layout)
//
//        val yes : TextView = dialog.findViewById(R.id.yes_btn)
//        val no : TextView = dialog.findViewById(R.id.no_btn)
//
//        yes.setOnClickListener {
//            auth.signOut()
//            findNavController().navigate(R.id.action_mainScreenFragment_to_loginFragment)
//        }
//        no.setOnClickListener {
//            dialog.dismiss()
//        }
//
//        dialog.show()


        }

        binding.settings.setOnClickListener{
            findNavController().navigate(R.id.action_mainScreenFragment_to_settingsFragment)
        }



//        if(flag == "0") {
//            binding.initialMsg.text = "No Contacts"
//        }
//        reference.child(auth.uid!!).child("name").addValueEventListener(object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                userName = snapshot.getValue(String::class.java).toString()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//
//        })


        binding.searchIcon.setOnClickListener{
            val searchText = binding.search.text.toString()
            binding.search.setText("")
            if(searchText.isEmpty()) {
                binding.search.error = "Enter Name"
            }
            else {
                binding.pB.visibility=View.VISIBLE

                reference.addValueEventListener( object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for(dataSnapshot: DataSnapshot in snapshot.children) {
                            val data: DataHolder? = dataSnapshot.getValue(DataHolder::class.java)
                             if(data?.uid!=auth.uid && data?.name?.contains(searchText) == true) {
                                binding.pB.visibility=View.INVISIBLE
                                 if(!userData.contains(data)) {
                                     temp = 1
                                     tempReference.child(data.uid).setValue(data)
                                     userData.add(data)
                                 }
                                 else {
                                     temp = 1
                                 }
                             }
                             else if(data?.uid==auth.uid){
                                 temp = 1
                               binding.pB.visibility=View.INVISIBLE
                             }
                            else {
                                 binding.pB.visibility=View.INVISIBLE
                                 temp = 0
                             }
                        }
                        if(temp==0) {
                            Toast.makeText(requireActivity(), "User Not Found", Toast.LENGTH_SHORT).show()
                        }
                        binding.homeRecyclerView.adapter?.notifyDataSetChanged()

                    }

                    override fun onCancelled(error: DatabaseError) {
                        Toast.makeText(requireActivity(), "Failed", Toast.LENGTH_SHORT).show()
                    }
                })
            }

        }


       // userData.add(DataHolder("100", "Amaan Ur Rahman", "amaanurrahman00@gmail.com", "Hey There I'm Using This App"))
//        val postListner = object : ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for (dataSnapshot: DataSnapshot in snapshot.children) {
//                        data = dataSnapshot.getValue(DataHolder::class.java)
//                    if(data!=null) {
//                        userData.add(data!!)
//                    }
//
//                }
//                binding.homeRecyclerView.adapter?.notifyDataSetChanged()
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//        }
//        reference.addValueEventListener(postListner)

        binding.homeRecyclerView.adapter = MainAdapter(this.requireActivity(), requireParentFragment(), userData, Model)
        binding.homeRecyclerView.layoutManager = LinearLayoutManager(this.requireActivity())

    }


}