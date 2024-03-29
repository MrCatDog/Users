package com.example.users.utils

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.users.utils.RecyclerAdapter.VH
import androidx.recyclerview.widget.RecyclerView
import com.example.users.R
import com.example.users.databinding.UsersListItemBinding
import com.example.users.model.domain.FullUserInfo.BaseUserInfo
import java.util.ArrayList

class RecyclerAdapter(private val listener: (BaseUserInfo) -> Unit) : RecyclerView.Adapter<VH>() {
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = UsersListItemBinding.bind(itemView)
    }

    private var items: List<BaseUserInfo> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        return VH(
            LayoutInflater.from(parent.context).inflate(R.layout.users_list_item, parent, false)
        )
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = items[position]
        holder.binding.usersListName.text = item.name
        holder.binding.usersListEmail.text = item.email

        holder.binding.usersListActive.apply {
            val context = holder.itemView.context
            text = if(item.isActive) {
                setTextColor(context.getColor(R.color.users_list_is_active_true))
                context.getString(R.string.users_list_isActive_true)
            } else {
                setTextColor(context.getColor(R.color.users_list_is_active_false))
                context.getString(R.string.users_list_isActive_false)
            }
        }

        holder.binding.root.setOnClickListener {
            listener(item)
        }
    }

    override fun getItemCount() = items.size

    @SuppressLint("NotifyDataSetChanged") //all of them changed
    fun setData(items: List<BaseUserInfo>) {
        this.items = items
        notifyDataSetChanged()
    }
}