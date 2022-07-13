package com.ebrahimi2723.neumorphismmusic.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ebrahimi2723.neumorphismmusic.staticdata.StaticData
import com.ebrahimi2723.neumorphismmusic.PlayingNowActivity
import com.ebrahimi2723.neumorphismmusic.databinding.RowMusicListBinding
import com.ebrahimi2723.neumorphismmusic.model.MusicFile

class MusicListAdapter(private val musicList: List<MusicFile>) :
    RecyclerView.Adapter<MusicListAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(
            RowMusicListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.items.musicName.text = musicList[position].title
        holder.items.musicName.setOnClickListener {

            val intent = Intent(it.context, PlayingNowActivity::class.java)

            StaticData.musicList = musicList
            StaticData.position = position
             intent.putExtra("list","list")

            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return musicList.size
    }


    class MyViewHolder(val items: RowMusicListBinding) : RecyclerView.ViewHolder(items.root) {


    }
}