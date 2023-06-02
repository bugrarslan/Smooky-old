package com.bugra.smooky.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bugra.smooky.databinding.RecyclerRowBinding
import com.bugra.smooky.model.Post
import com.bugra.smooky.view.FeedFragmentDirections
import com.squareup.picasso.Picasso

class FeedRecyclerAdapter(val postList: ArrayList<Post>) : RecyclerView.Adapter<FeedRecyclerAdapter.PostHolder>() {

    class PostHolder(val binding: RecyclerRowBinding) : ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostHolder {
        val binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PostHolder(binding)
    }

    override fun getItemCount(): Int {
        return postList.size
    }

    override fun onBindViewHolder(holder: PostHolder, position: Int) {
        holder.binding.recyclerUsernameText.text = postList.get(position).username
        holder.binding.recyclerCommentText.text = postList.get(position).comment
        Picasso.get().load(postList.get(position).downloadUrl).into(holder.binding.recyclerImageView)
        val username = postList.get(position).username
        val comment = postList.get(position).comment
        val downloadUrl = postList.get(position).downloadUrl

        holder.itemView.setOnClickListener {
            val action = FeedFragmentDirections.actionFeedFragmentToUploadFragment(
                "old",
                "$username",
                "$comment",
                "$downloadUrl"
            )
            Navigation.findNavController(it).navigate(action)
        }
    }
}