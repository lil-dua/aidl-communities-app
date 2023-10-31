package com.advanced.mockserver.ui.home.user

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.advanced.mockserver.User
import com.advanced.mockclient.databinding.ItemUserBinding
import com.advanced.mockserver.ui.home.HomeFragment

/***
 * Created by HoangRyan aka LilDua on 10/28/2023.
 */
class UserAdapter (
    private val homeFragment: HomeFragment
) : RecyclerView.Adapter<UserAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemUserBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.user = user
            binding.imageContact.setImageResource(user.image)
        }
    }

    //init list user
    private var users: List<User> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemUserBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.bind(user)

        //handle on long click
        holder.itemView.setOnLongClickListener {
            homeFragment.removeUserWithId(user.id)
            true
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun setData(newList: List<User>) {
        val diffCallback = UserDiffCallback(users, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)
        users = newList
        diffResult.dispatchUpdatesTo(this)
    }

}