package com.movie.app.ui.fragments

import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.movie.app.R
import com.movie.app.adapters.ActorsAdapter
import com.movie.app.common.Constants
import com.movie.app.data.source.local.SharedPrefs
import com.movie.app.databinding.AlertDialogBinding
import com.movie.app.databinding.FragmentMovieDetailsBinding
import com.movie.app.model.MovieModel
import java.lang.NumberFormatException


private const val ARG_PARAM1 = "movie"


class MovieDetailsFragment : Fragment() {
    private lateinit var fragmentMovieDetailsBinding: FragmentMovieDetailsBinding

    private var movie: MovieModel? = null
    private var isWatched:Boolean = false
    //private var movieBackground: Array<ActorModel>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            movie = it.getParcelable(ARG_PARAM1)
            //movieBackground = it.getParcelableArray(ARG_PARAM2) as Array<ActorModel>?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentMovieDetailsBinding =
            FragmentMovieDetailsBinding.inflate(inflater, container, false)
        return fragmentMovieDetailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val actorsAdapter = ActorsAdapter()
        fragmentMovieDetailsBinding.rvCac.adapter = actorsAdapter
        movie?.let { movieModel ->
            with(fragmentMovieDetailsBinding) {
                Glide.with(ivMovie.context).load(movieModel.poster)
                    .apply(
                        //https://stackoverflow.com/questions/4743116/get-screen-width-and-height-in-android
                        RequestOptions().fitCenter().override(Resources.getSystem().displayMetrics.widthPixels,
                            (Resources.getSystem().displayMetrics.heightPixels*0.35).toInt()
                        )
                    )
                    .transform(GranularRoundedCorners(0f, 0f, 32f, 32f))
                    .into(ivMovie)
                tvRateThis.setOnClickListener {
                    showRateDialog()
                }
                tvTenScore.text = getString(R.string._9_9)
                tvHundredScore.text = getString(R.string._150)
                tvMetaScore.text = getString(R.string._99)
                tvMetaScoreReviews.text = getString(R.string._62_critic_reviews)
                tvMovieTitle.text = movieModel.title
                val movieYearTimeText = "${movieModel.year} ${movieModel.runtime}"
                tvMovieYearTime.text = movieYearTimeText
                tvSummary.text = movieModel.description
                actorsAdapter.submitList(movieModel.actors)
                for (chipName in movieModel.genres.split("|")) {
                    val chip = Chip(requireContext())
                    chip.text = chipName
                    val drawable =
                        ChipDrawable.createFromAttributes(requireContext(), null, 0, R.style.ChipTheme2)
                    chip.setChipDrawable(drawable)
                    fragmentMovieDetailsBinding.cgTypes.addView(chip)
                }
                isWatched = SharedPrefs().getParam(
                    requireContext(),
                    movieModel.title
                )
                fragmentMovieDetailsBinding.btnAdd.setImageResource(if(isWatched) R.drawable.tick else R.drawable.add)

            }
        }

        fragmentMovieDetailsBinding.btnAdd.setOnClickListener {
            movie?.let {
                isWatched = !isWatched
                val sharedPrefs  = SharedPrefs()
                sharedPrefs.setParam(
                    requireContext(),
                    it.title,
                    isWatched
                )
                Toast.makeText(
                    requireContext(),
                    if(isWatched) "Added to watch" else "Removed from watch",
                    Toast.LENGTH_SHORT
                ).show()

                if(isWatched){

                    addToWatchList(requireContext(),sharedPrefs,it.title)
                }else{
                    removeFromWatchList(requireContext(),sharedPrefs,it.title)

                }
                fragmentMovieDetailsBinding.btnAdd.setImageResource(if(isWatched) R.drawable.tick else R.drawable.add)
            }
        }



    }

    private fun showRateDialog() {
        val alertDialogBinding = AlertDialogBinding.inflate(LayoutInflater.from(context))
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        alertDialogBuilder.setView(alertDialogBinding.root)
        val alertDialog = alertDialogBuilder.create()

        alertDialogBinding.btnRate.setOnClickListener {
            val text = alertDialogBinding.etRate.text.toString()
            val message:String = if (text.isNotEmpty() || text.isNotBlank()) {
                try {
                    val rate = text.toInt()
                    if (rate < 0 || rate > 5) {
                        "Rate is not in range [0-5]"
                    } else {
                        text
                    }
                } catch (e: NumberFormatException) {
                    "Number Format Error"
                }
            } else {
                "Empty or Blank"
            }
            Toast.makeText(requireContext(),message,Toast.LENGTH_SHORT).show()
            alertDialog.dismiss()
        }

        alertDialogBinding.btnCancel.setOnClickListener {
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun removeFromWatchList(context: Context, sharedPrefs: SharedPrefs, title: String) {
        var index = sharedPrefs.getParam<Int>(context,Constants.MENU_INDEX)
        val currentIndex = sharedPrefs.getParamMenuName(
            context,
            title,
            index
        )
        sharedPrefs.reorderParamMenu(
            context,
            currentIndex,
            index
        )
        sharedPrefs.setParam(
            context,
            Constants.MENU_INDEX,
            --index
        )

    }

    private fun addToWatchList(context: Context, sharedPrefs: SharedPrefs, title: String) {
        var index = sharedPrefs.getParam<Int>(context,Constants.MENU_INDEX)
        if(index<0) index = 0
        sharedPrefs.setParam(
            context,
            "${Constants.MENU_ITEM_SUFFIX}${index}",
            title
        )
        sharedPrefs.setParam(
            context,
            Constants.MENU_INDEX,
            ++index
        )

    }
}