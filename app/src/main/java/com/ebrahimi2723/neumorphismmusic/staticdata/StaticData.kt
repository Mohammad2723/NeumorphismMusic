package com.ebrahimi2723.neumorphismmusic.staticdata

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.ebrahimi2723.neumorphismmusic.model.MusicFile

class StaticData {
    companion object{
        var mediaPlayer: MediaPlayer = MediaPlayer()
        var musicList:List<MusicFile> = ArrayList()
        var position:Int = 0
        var musicCount:Int = 0

    }
}