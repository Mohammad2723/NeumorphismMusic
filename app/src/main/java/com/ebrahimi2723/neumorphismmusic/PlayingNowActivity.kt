package com.ebrahimi2723.neumorphismmusic

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ebrahimi2723.neumorphismmusic.databinding.ActivityPlayingNowBinding
import com.ebrahimi2723.neumorphismmusic.staticdata.StaticData
import java.lang.Thread.sleep


class PlayingNowActivity : AppCompatActivity(), SeekBar.OnSeekBarChangeListener {


    private lateinit var binding: ActivityPlayingNowBinding

    @SuppressLint("Recycle")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayingNowBinding.inflate(layoutInflater)
        setContentView(binding.root)


        /*Animation Music cover */

        val anim = ObjectAnimator.ofFloat(binding.coverImage, "rotation", 0.5f, 360f)
        anim.duration = 30000
        anim.repeatCount = 1000000000
        anim.repeatMode = ObjectAnimator.RESTART
        anim.start()


        /* play music*/

        if (StaticData.mediaPlayer.isPlaying && intent.getStringExtra("list") == "list") {
            StaticData.mediaPlayer.stop()
            StaticData.mediaPlayer.release()
            StaticData.mediaPlayer = MediaPlayer.create(
                this,
                Uri.parse(StaticData.musicList[StaticData.position].data)
            )
            StaticData.mediaPlayer.start()
            updatedMeta()
            updateSeekbar()

        } else if (!StaticData.mediaPlayer.isPlaying && intent.getStringExtra("list") == "list") {
            StaticData.mediaPlayer = MediaPlayer.create(
                this,
                Uri.parse(StaticData.musicList[StaticData.position].data)
            )
            StaticData.mediaPlayer.start()
            updatedMeta()
            updateSeekbar()
        } else if (!StaticData.mediaPlayer.isPlaying && intent.getStringExtra("list") != "list") {
            StaticData.mediaPlayer = MediaPlayer.create(
                this,
                Uri.parse(StaticData.musicList[StaticData.position].data)
            )
            updatedMeta()
            updateSeekbar()
            binding.play.setImageResource(R.drawable.play_24)
        } else if (StaticData.mediaPlayer.isPlaying && intent.getStringExtra("list") != "list") {
            updatedMeta()
            updateSeekbar()
        }

        /*after playing music we set metadata*/

//        updatedMeta()
        /* what happen when play button is pressed*/

        binding.play.setOnClickListener {

            if (!StaticData.mediaPlayer.isPlaying) {
                StaticData.mediaPlayer.start()
                binding.play.setImageResource(R.drawable.pause_24)
                anim.resume()
                updatedMeta()

            } else {
                StaticData.mediaPlayer.pause()
                binding.play.setImageResource(R.drawable.play_24)
                anim.pause()
            }

        }

        /* set seekbar when music is playing */
        updateSeekbar()
        binding.progress.max = StaticData.mediaPlayer.duration


        //set time of music

        binding.maxTime.text = time(StaticData.mediaPlayer.duration)


        /* what happen when next button is pressed*/

        binding.next.setOnClickListener {
            nextMusic()
        }


        /* what happen when preview button is pressed*/

        binding.preview.setOnClickListener {
            if (StaticData.position != 0) {
                StaticData.position--
            } else {
                StaticData.position = StaticData.musicCount - 1
            }

            StaticData.mediaPlayer.stop()
            StaticData.mediaPlayer.release()
            StaticData.mediaPlayer = MediaPlayer
                .create(this, Uri.parse(StaticData.musicList[StaticData.position].data))
            StaticData.mediaPlayer.start()
            updatedMeta()
            updateSeekbar()
            binding.progress.max = StaticData.mediaPlayer.duration

        }

        /* what happen when user swap on seekBar button is pressed */
        binding.progress.setOnSeekBarChangeListener(this)


        autoNextMusic()

    }

    private fun autoNextMusic() {
        StaticData.mediaPlayer.setOnCompletionListener {
            nextMusic()
            autoNextMusic()
        }
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
        updatedMeta()
        updateSeekbar()
        binding.progress.max = StaticData.mediaPlayer.duration
    }


    private fun updateSeekbar() {

        val updatedSeekbar = Thread() {
            run {
                val max = StaticData.mediaPlayer.duration
                var current = StaticData.mediaPlayer.currentPosition

                while (current < max) {
                    try {
                        sleep(500)
                        current = StaticData.mediaPlayer.currentPosition
                        binding.progress.progress = current

                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }


            }
        }
        updatedSeekbar.start()


    }

    private fun setCoverMusic() {

        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(StaticData.musicList[StaticData.position].data)
        val data = mmr.embeddedPicture

        if (data != null) {
            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            binding.coverImage.setImageBitmap(bitmap)
            binding.coverImage.scaleType = ImageView.ScaleType.CENTER_CROP
        } else {
            binding.coverImage.setImageResource(R.drawable.music_24)
            binding.coverImage.scaleType = ImageView.ScaleType.FIT_CENTER
        }
    }

    private fun updatedMeta() {
        setCoverMusic()
        binding.artist.text = StaticData.musicList[StaticData.position].artist
        binding.trackName.text = StaticData.musicList[StaticData.position].title
    }

    private fun time(duration: Int): String {

        val min = duration / 1000 / 60
        val sec = duration / 1000 % 60

        var time = "$min:$sec"
        if (sec < 10) {
            time = "$min:0$sec"
        }
        return time

    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        if (seekBar != null) {
            StaticData.mediaPlayer.seekTo(seekBar.progress)
        }
    }

}