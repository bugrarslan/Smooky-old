package com.bugra.smooky.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.bugra.smooky.adapter.FeedRecyclerAdapter
import com.bugra.smooky.databinding.FragmentFeedBinding
import com.bugra.smooky.model.Post
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FeedFragment : Fragment() {

    private lateinit var binding: FragmentFeedBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var postList: ArrayList<Post>
    private lateinit var feedAdapter: FeedRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        firestore = Firebase.firestore
        postList = ArrayList()

        getData()

        binding.feedRecyclerView.layoutManager = LinearLayoutManager(requireActivity())
        feedAdapter = FeedRecyclerAdapter(postList)
        binding.feedRecyclerView.adapter = feedAdapter
    }

    private fun getData() {

        firestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener { value, error ->

            if (error != null) {

                Toast.makeText(requireActivity(), error.localizedMessage, Toast.LENGTH_LONG).show()

            } else {

                if (value != null) {

                    if (!value.isEmpty) {

                        val documents = value.documents

                        postList.clear()

                        for (document in documents) {
                            //casting
                            val comment = document.get("comment") as String
                            val userEmail = document.get("userEmail") as String
                            val downloadUrl = document.get("downloadUrl") as String

                            val post = Post(userEmail, comment, downloadUrl)
                            postList.add(post)
                        }

                        feedAdapter.notifyDataSetChanged()

                    }
                }
            }
        }
    }
}