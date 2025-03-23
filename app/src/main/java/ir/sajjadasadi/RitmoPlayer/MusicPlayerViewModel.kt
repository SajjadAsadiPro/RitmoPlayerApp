package ir.sajjadasadi.RitmoPlayer.viewmodel

import android.app.Application
import android.content.ContentResolver
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import ir.sajjadasadi.RitmoPlayer.MusicItem
import ir.sajjadasadi.RitmoPlayer.loadMusic
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MusicPlayerViewModel(application: Application, contentResolver: ContentResolver) :
    AndroidViewModel(application) {

    private val _musicList = MutableStateFlow(loadMusic(contentResolver, application).sortedBy { it.date })
    val musicList: StateFlow<List<MusicItem>> = _musicList

    private val _currentMusic = MutableStateFlow<MusicItem?>(null)
    val currentMusic: StateFlow<MusicItem?> = _currentMusic

    val exoPlayer: ExoPlayer = ExoPlayer.Builder(application).build()

    var isLooping = MutableStateFlow(false)
    var isShuffling = MutableStateFlow(false)
    var isSearchVisible = MutableStateFlow(false)
    var searchText = MutableStateFlow("")

    init {
        exoPlayer.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_ENDED) {
                    playNext()
                }
            }
        })
    }

    fun toggleShuffle() {
        isShuffling.value = !isShuffling.value
        viewModelScope.launch {
            _musicList.emit(if (isShuffling.value) _musicList.value.shuffled() else _musicList.value.sortedBy { it.date })
        }
    }

    fun playMusic(music: MusicItem) {
        _currentMusic.value = music
        exoPlayer.setMediaItem(MediaItem.fromUri(music.uri))
        exoPlayer.prepare()
        exoPlayer.play()
    }

    private fun playNext() {
        val list = _musicList.value
        val currentIndex = list.indexOf(_currentMusic.value)
        if (currentIndex != -1 && currentIndex < list.size - 1) {
            playMusic(list[currentIndex + 1])
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer.release()
    }
}