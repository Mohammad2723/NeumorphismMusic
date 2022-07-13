package com.ebrahimi2723.neumorphismmusic

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.ebrahimi2723.neumorphismmusic.adapter.MusicListAdapter
import com.ebrahimi2723.neumorphismmusic.databinding.ActivityMusicListBinding
import com.ebrahimi2723.neumorphismmusic.model.MusicFile
import com.ebrahimi2723.neumorphismmusic.staticdata.StaticData


class MusicList : AppCompatActivity() {
    private lateinit var binding: ActivityMusicListBinding


    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityMusicListBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        /* first check file permission*/
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            showList()
            updateMeta()
            autoNextMusic()
        } else {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2723)
        }

        /* actionBar  Paying Now*/
        binding.miniMusicLay.setOnClickListener {
            startActivity(Intent(this, PlayingNowActivity::class.java))
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 2723 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            showList()
            updateMeta()
            autoNextMusic()

        }
    }

    /* set data to recycler view */
    private fun showList() {
        val adapter = MusicListAdapter(listMusic())
        binding.recyclerMusicList.adapter = adapter
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerMusicList.layoutManager = layoutManager
    }


    /* create a Array list of MusicFile -> MusicFile is model */
    @SuppressLint("Recycle")


    private fun listMusic(): List<MusicFile> {

        val selection = MediaStore.Audio.Media.IS_MUSIC + " != 0"

        val projection = arrayOf(

            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DURATION,


            )

        val cursor = contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            null
        )

        val songs = ArrayList<MusicFile>()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                /*  StaticData.musicCount++ save  how many music is ArrayList - > int */


                StaticData.musicCount++
                songs.add(

                    MusicFile(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(5),

                        )
                )
            }
        }
        StaticData.musicList = songs.sortedBy { it.title }
        return StaticData.musicList
    }


    private fun autoNextMusic() {
        StaticData.mediaPlayer.setOnCompletionListener {
            nextMusic()
            updateMeta()
            autoNextMusic()
        }
    }

    private fun updateMeta() {
        binding.titleText.text = StaticData.musicList[StaticData.position].title


        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(StaticData.musicList[StaticData.position].data)
        val data = mmr.embeddedPicture

        if (data != null) {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            binding.actionBarCover.setImageBitmap(bitmap)
        } else {
            binding.actionBarCover.setImageResource(R.drawable.music_24)
        }
    }

    override fun onRestart() {
        super.onRestart()
        updateMeta()
        autoNextMusic()
    }


    private fun nextMusic() {
        if (StaticData.position != StaticData.musicCount - 1) {
            StaticData.position++
            Toast.makeText(this, "${StaticData.position}", Toast.LENGTH_SHORT).show()

        } else {
            StaticData.position = 0
            Toast.makeText(this, "${StaticData.position}", Toast.LENGTH_SHORT).show()

        }
        StaticData.mediaPlayer.stop()
        StaticData.mediaPlayer.release()
        StaticData.mediaPlayer = MediaPlayer
            .create(this, Uri.parse(StaticData.musicList[StaticData.position].data))
        StaticData.mediaPlayer.start()
    }

}