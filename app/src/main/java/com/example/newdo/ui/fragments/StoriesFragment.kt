package com.example.newdo.ui.fragments

import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.util.SparseArray
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import at.huber.youtubeExtractor.VideoMeta
import at.huber.youtubeExtractor.YouTubeExtractor
import at.huber.youtubeExtractor.YtFile
import com.example.newdo.R
import com.example.newdo.adapters.StoriesAdapter
import com.example.newdo.database.model.Story
import com.example.newdo.databinding.FragmentStoriesBinding
import com.example.newdo.ui.MainActivity
import com.example.newdo.ui.viewmodels.NewsViewModel
import com.example.newdo.utils.Constants.Companion.YOUTUBE_AUDIO_TAG
import com.example.newdo.utils.Constants.Companion.YOUTUBE_I_TAG
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.MergingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import com.google.android.exoplayer2.util.Util

class StoriesFragment : Fragment(R.layout.fragment_stories) {

    //hard code demo urls temporally
    private lateinit var binding: FragmentStoriesBinding

    lateinit var viewModel: NewsViewModel
    lateinit var storyAdapter: StoriesAdapter
    private lateinit var myStoryList: ArrayList<Story>

    private var spanCount: Int = 1

    private var exoPlayer: SimpleExoPlayer? = null
    private var playOnReady = true
    private var currentWindow = 0
    private var playbackPosition: Long = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentStoriesBinding.bind(view)

        viewModel = (activity as MainActivity).viewModel

        observeDarkMode()

        setUpRecyclerViews()

        //pass url to the media player
        storyAdapter.setOnStoryClickListener {

        }


    }


    private fun setUpRecyclerViews() {

        binding.trendingRecyclerView.apply {
            storyAdapter = StoriesAdapter(requireContext())
            adapter = storyAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

            //init list
            myStoryList = ArrayList()

            //add data
            myStoryList.add(
                Story(
                    R.drawable.ic_launcher_foreground,
                    "Hello this is me on the other side",
                    "https://r3---sn-5hnednlr.googlevideo.com/videoplayback?expire=1627965692&ei=nHQIYYidK4-P-gaLoJDIDA&ip=88.12.19.206&id=o-AIYLAlSNWie1ZgHgPobNlO3aiR1kyx6IsZXa7NV-VMi9&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=ugaM6nQnrGkpakePU8eialEG&gir=yes&clen=2647715&ratebypass=yes&dur=120.697&lmt=1623060508295199&fexp=24001373,24007246&c=WEB&txp=6310222&n=eGNRWqCSfVZXcUE7P&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdW2JJfdW68DSe-wxP3yWlJN6LAQhk4aJMd8QK-uu3hoCIC-KxP3wxYpvV0SLOAE1hE5qj0VotoH-StfXxNhHIOfD&cm2rm=sn-h5qzy7s&req_id=714761e0c44ba3ee&redirect_counter=2&rm=sn-5hneer7s&cms_redirect=yes&ipbypass=yes&mh=1A&mip=197.210.76.151&mm=34&mn=sn-5hnednlr&ms=ltu&mt=1627944036&mv=m&mvi=3&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgHUP3n4BwZQnDO5PiTLLrIj36ym8V2EvazCGTJm079RYCIQCAEvuTCHCUCdnhMBFjMQBRpfpTa4zhC2I4QilY1_PAAA%3D%3D"
                )
            )
            myStoryList.add(
                Story(
                    R.drawable.ic_launcher_background,
                    "Meet the latest news app",
                    "https://r3---sn-5hnednlr.googlevideo.com/videoplayback?expire=1627965692&ei=nHQIYYidK4-P-gaLoJDIDA&ip=88.12.19.206&id=o-AIYLAlSNWie1ZgHgPobNlO3aiR1kyx6IsZXa7NV-VMi9&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=ugaM6nQnrGkpakePU8eialEG&gir=yes&clen=2647715&ratebypass=yes&dur=120.697&lmt=1623060508295199&fexp=24001373,24007246&c=WEB&txp=6310222&n=eGNRWqCSfVZXcUE7P&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdW2JJfdW68DSe-wxP3yWlJN6LAQhk4aJMd8QK-uu3hoCIC-KxP3wxYpvV0SLOAE1hE5qj0VotoH-StfXxNhHIOfD&cm2rm=sn-h5qzy7s&req_id=714761e0c44ba3ee&redirect_counter=2&rm=sn-5hneer7s&cms_redirect=yes&ipbypass=yes&mh=1A&mip=197.210.76.151&mm=34&mn=sn-5hnednlr&ms=ltu&mt=1627944036&mv=m&mvi=3&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgHUP3n4BwZQnDO5PiTLLrIj36ym8V2EvazCGTJm079RYCIQCAEvuTCHCUCdnhMBFjMQBRpfpTa4zhC2I4QilY1_PAAA%3D%3D"
                )
            )
            myStoryList.add(
                Story(
                    R.drawable.ic_demo_bg,
                    "Meet the latest news app",
                    "https://r3---sn-5hnednlr.googlevideo.com/videoplayback?expire=1627965692&ei=nHQIYYidK4-P-gaLoJDIDA&ip=88.12.19.206&id=o-AIYLAlSNWie1ZgHgPobNlO3aiR1kyx6IsZXa7NV-VMi9&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=ugaM6nQnrGkpakePU8eialEG&gir=yes&clen=2647715&ratebypass=yes&dur=120.697&lmt=1623060508295199&fexp=24001373,24007246&c=WEB&txp=6310222&n=eGNRWqCSfVZXcUE7P&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdW2JJfdW68DSe-wxP3yWlJN6LAQhk4aJMd8QK-uu3hoCIC-KxP3wxYpvV0SLOAE1hE5qj0VotoH-StfXxNhHIOfD&cm2rm=sn-h5qzy7s&req_id=714761e0c44ba3ee&redirect_counter=2&rm=sn-5hneer7s&cms_redirect=yes&ipbypass=yes&mh=1A&mip=197.210.76.151&mm=34&mn=sn-5hnednlr&ms=ltu&mt=1627944036&mv=m&mvi=3&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgHUP3n4BwZQnDO5PiTLLrIj36ym8V2EvazCGTJm079RYCIQCAEvuTCHCUCdnhMBFjMQBRpfpTa4zhC2I4QilY1_PAAA%3D%3D"
                )
            )
            myStoryList.add(
                Story(
                    R.drawable.ic_demo_bg,
                    "Meet the latest news app",
                    "https://r3---sn-5hnednlr.googlevideo.com/videoplayback?expire=1627965692&ei=nHQIYYidK4-P-gaLoJDIDA&ip=88.12.19.206&id=o-AIYLAlSNWie1ZgHgPobNlO3aiR1kyx6IsZXa7NV-VMi9&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=ugaM6nQnrGkpakePU8eialEG&gir=yes&clen=2647715&ratebypass=yes&dur=120.697&lmt=1623060508295199&fexp=24001373,24007246&c=WEB&txp=6310222&n=eGNRWqCSfVZXcUE7P&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdW2JJfdW68DSe-wxP3yWlJN6LAQhk4aJMd8QK-uu3hoCIC-KxP3wxYpvV0SLOAE1hE5qj0VotoH-StfXxNhHIOfD&cm2rm=sn-h5qzy7s&req_id=714761e0c44ba3ee&redirect_counter=2&rm=sn-5hneer7s&cms_redirect=yes&ipbypass=yes&mh=1A&mip=197.210.76.151&mm=34&mn=sn-5hnednlr&ms=ltu&mt=1627944036&mv=m&mvi=3&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgHUP3n4BwZQnDO5PiTLLrIj36ym8V2EvazCGTJm079RYCIQCAEvuTCHCUCdnhMBFjMQBRpfpTa4zhC2I4QilY1_PAAA%3D%3D"
                )
            )
            myStoryList.add(
                Story(
                    R.drawable.ic_demo_bg,
                    "Meet the latest news app",
                    "https://r3---sn-5hnednlr.googlevideo.com/videoplayback?expire=1627965692&ei=nHQIYYidK4-P-gaLoJDIDA&ip=88.12.19.206&id=o-AIYLAlSNWie1ZgHgPobNlO3aiR1kyx6IsZXa7NV-VMi9&itag=18&source=youtube&requiressl=yes&vprv=1&mime=video%2Fmp4&ns=ugaM6nQnrGkpakePU8eialEG&gir=yes&clen=2647715&ratebypass=yes&dur=120.697&lmt=1623060508295199&fexp=24001373,24007246&c=WEB&txp=6310222&n=eGNRWqCSfVZXcUE7P&sparams=expire%2Cei%2Cip%2Cid%2Citag%2Csource%2Crequiressl%2Cvprv%2Cmime%2Cns%2Cgir%2Cclen%2Cratebypass%2Cdur%2Clmt&sig=AOq0QJ8wRAIgdW2JJfdW68DSe-wxP3yWlJN6LAQhk4aJMd8QK-uu3hoCIC-KxP3wxYpvV0SLOAE1hE5qj0VotoH-StfXxNhHIOfD&cm2rm=sn-h5qzy7s&req_id=714761e0c44ba3ee&redirect_counter=2&rm=sn-5hneer7s&cms_redirect=yes&ipbypass=yes&mh=1A&mip=197.210.76.151&mm=34&mn=sn-5hnednlr&ms=ltu&mt=1627944036&mv=m&mvi=3&pl=24&lsparams=ipbypass,mh,mip,mm,mn,ms,mv,mvi,pl&lsig=AG3C_xAwRQIgHUP3n4BwZQnDO5PiTLLrIj36ym8V2EvazCGTJm079RYCIQCAEvuTCHCUCdnhMBFjMQBRpfpTa4zhC2I4QilY1_PAAA%3D%3D"
                )
            )


            storyAdapter.stories = myStoryList
        }

    }

    private fun observeDarkMode() {
        when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_NO -> {
                binding.pageTitle.setTextColor(Color.parseColor("#131313"))
            } // Light mode is active

            Configuration.UI_MODE_NIGHT_YES -> {
            } // Night mode is active
        }
    }






}