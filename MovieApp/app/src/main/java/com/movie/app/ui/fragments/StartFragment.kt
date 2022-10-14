package com.movie.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.navigation.NavigationView
import com.movie.app.R
import com.movie.app.adapters.MovieAdapter
import com.movie.app.adapters.TabAdapter
import com.movie.app.common.Constants
import com.movie.app.data.repository.MovieRepository
import com.movie.app.data.source.local.SharedPrefs
import com.movie.app.ui.viewmodel.MovieViewModel
import com.movie.app.ui.viewmodel.ViewModelFactory
import com.movie.app.ui.viewmodel.model.MovieTypes
import com.movie.app.ui.viewmodel.model.Status
import com.movie.app.databinding.FragmentStartBinding


class StartFragment : Fragment() {
    private lateinit var fragmentStartBinding: FragmentStartBinding
    private lateinit var movieModel: MovieViewModel
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var tabAdapter: TabAdapter
    private var currentTabIndex = MovieTypes.THEATER.type

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            currentTabIndex = it.getInt(Constants.MOVIE_TYPES_TAB_INDEX)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentStartBinding = FragmentStartBinding.inflate(inflater, container, false)
        return fragmentStartBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //https://medium.com/@ezatpanah/navigation-drawer-and-drawer-layout-in-kotlin-in-depth-guide-103ce411416d
        fragmentStartBinding.ivMenu.setOnClickListener {
            fragmentStartBinding.dlMenu.open()
        }
        fragmentStartBinding.svMovie.visibility = View.INVISIBLE
        fragmentStartBinding.svMovie.isActivated = false
        fragmentStartBinding.ivSearch.setOnClickListener {
            fragmentStartBinding.svMovie.visibility =
                if (fragmentStartBinding.svMovie.visibility == View.VISIBLE) View.INVISIBLE else View.VISIBLE
            fragmentStartBinding.svMovie.isActivated = !fragmentStartBinding.svMovie.isActivated

        }


        fragmentStartBinding.srlMovie.setOnRefreshListener {
            movieModel.getMovies(currentTabIndex)
            fragmentStartBinding.srlMovie.isRefreshing = false
        }
        val viewModelProvider = ViewModelFactory(MovieRepository()) { error ->
            error?.let {
                Toast.makeText(
                    requireContext(),
                    it,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
        movieModel =
            ViewModelProvider(requireActivity(), viewModelProvider)[MovieViewModel::class.java]
        movieModel.getMovies(currentTabIndex)


        movieAdapter = MovieAdapter { movieModel ->
            val action = StartFragmentDirections.actionStartFragmentToMovieDetailsFragment(
                movieModel

            )
            findNavController().navigate(action)
        }


        fragmentStartBinding.cgTypes.setOnCheckedStateChangeListener { group, checkedIds ->
            if (checkedIds.size == 0) {
                val text = fragmentStartBinding.svMovie.query.toString()
                //unselected chip && search text
                if(text.isNotEmpty() || text.isNotBlank()){
                    movieAdapter.searchMovieByName(text,-1,null)
                }else {
                    //unselected chip only
                    movieAdapter.resetAllList()
                }
                return@setOnCheckedStateChangeListener
            }
            val chip: Chip = group.findViewById(checkedIds[0])
            chip?.let {
                Toast.makeText(
                    requireContext(),
                    it.text,
                    Toast.LENGTH_SHORT
                ).show()
                //selected chip && search text(can be empty)
                movieAdapter.searchMovieByType(it.text.toString() ,fragmentStartBinding.svMovie.query.toString())
            }

        }
        fragmentStartBinding.svMovie.setOnQueryTextListener(object : SearchView.OnQueryTextListener,
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    Toast.makeText(
                        requireContext(),
                        it,
                        Toast.LENGTH_SHORT
                    ).show()
                    val ids = fragmentStartBinding.cgTypes.checkedChipIds
                    var id:Int = -1
                    var text:String? = null
                    if(ids.size>0){
                        for(item in ids){
                            id = item
                            text = fragmentStartBinding.cgTypes.findViewById<Chip>(item).text as String
                        }
                    }
                    // search text && selected chip (can be non selected)
                    movieAdapter.searchMovieByName(it,id,text)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrBlank() || newText.isEmpty()) {

                    //fragmentStartBinding.cgTypes.clearCheck()
                    val ids = fragmentStartBinding.cgTypes.checkedChipIds

                    var text:String? = null
                    if(ids.size>0){
                        for(item in ids){

                            text = fragmentStartBinding.cgTypes.findViewById<Chip>(item).text as String
                        }
                    }
                    text?.let {
                        //empty search && selected chip
                        if(it.isNotEmpty() || it.isNotBlank()){
                            movieAdapter.searchMovieByType(it,"")
                        }else{
                            //clear search && no selected chip
                            movieAdapter.resetAllList()
                        }
                    }?: run{
                        //empty search && selected chip
                        movieAdapter.resetAllList()
                    }


                }else{
                    val ids = fragmentStartBinding.cgTypes.checkedChipIds
                    var id:Int = -1
                    var text:String? = null
                    if(ids.size>0){
                        for(item in ids){
                            id = item
                            text = fragmentStartBinding.cgTypes.findViewById<Chip>(item).text as String
                        }
                    }
                    //search text && selected chip (can be non selected)
                    movieAdapter.searchMovieByName(newText,id,text)
                }
                return false
            }
        })

        fragmentStartBinding.rvMovies.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val linearLayoutManager =
                    fragmentStartBinding.rvMovies.layoutManager as LinearLayoutManager
                val visiblePosition = linearLayoutManager.findFirstVisibleItemPosition()
                //W/RecyclerView: Cannot call this method in a scroll callback.
                //Scroll callbacks might be run during a measure & layout pass where you cannot change theRecyclerView data.
                //Any method call that might change the structure of the RecyclerView or the adapter contents should be postponed to the next frame.
                //https://medium.com/@shahbaz8x/cannot-call-this-method-in-a-scroll-callback-9beaf9abc2db
                //bug -> from empty list to list with item(s)
                fragmentStartBinding.rvMovies.post {
                    movieAdapter.setCurrentItemIndex(visiblePosition)
                }
            }
        })

        fragmentStartBinding.rvMovies.adapter = movieAdapter

        tabAdapter = TabAdapter { position ->
            fragmentStartBinding.cgTypes.clearCheck()
            when (position) {
                MovieTypes.THEATER.type -> {
                    movieModel.getMovies(MovieTypes.THEATER.type)
                    currentTabIndex = MovieTypes.THEATER.type
                }
                MovieTypes.BOX_OFFICE.type -> {
                    movieModel.getMovies(MovieTypes.BOX_OFFICE.type)
                    currentTabIndex = MovieTypes.BOX_OFFICE.type
                }
                MovieTypes.COMING_SOON.type -> {
                    movieModel.getMovies(MovieTypes.COMING_SOON.type)
                    currentTabIndex = MovieTypes.COMING_SOON.type
                }
            }

        }
        tabAdapter.submitList(listOf("In Theater", "Box Office", "Coming Soon"))
        tabAdapter.setCurrentTab(currentTabIndex)
        fragmentStartBinding.rvTab.adapter = tabAdapter
        movieModel.movieListVal.observe(viewLifecycleOwner, Observer { resource ->
            if (resource.status == Status.SUCCESS) {
                val moviesModel = resource.data
                moviesModel?.let {
                    movieAdapter.submitList(it)
                    movieAdapter.addList(it)
                }

            } else if (resource.status == Status.ERROR) {
                Toast.makeText(requireContext(), resource.message, Toast.LENGTH_SHORT).show()
            }

        })

        //if the item is in the adapter list there is a possibility to display the item
        //there is no search function implemented by title in the api
        //can fetch and search all data, but what if data is large
        //but this api is small could be implement with MediatorLiveData with getMovies()
        //+ getBoxOffice() + getComingSoon()
        //https://developer.android.com/reference/androidx/lifecycle/MediatorLiveData
        fragmentStartBinding.nvMenu.setNavigationItemSelectedListener(object:NavigationView.OnNavigationItemSelectedListener{
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                val title = item.title.toString()
                val movie = movieAdapter.getWatchListMovie(title)
                if(movie?.size==0) {
                    Toast.makeText(requireContext(),getString(R.string.not_found),Toast.LENGTH_SHORT).show()
                    return false
                }
                movie?.first()?.let {
                    val action = StartFragmentDirections.actionStartFragmentToMovieDetailsFragment(it)
                    findNavController().navigate(action)
                    return true
                }
                Toast.makeText(requireContext(),getString(R.string.not_found),Toast.LENGTH_SHORT).show()
                return false
            }

        })
    }

    private fun updateMenuItems() {
        val sharedPrefs = SharedPrefs()
        val index = sharedPrefs.getParamMenuIndex(requireContext())
        fragmentStartBinding.nvMenu.menu.clear()
        for (value in 0 until index) {

            fragmentStartBinding.nvMenu.menu.add(
                sharedPrefs.getParamString(
                    requireContext(),
                    "${Constants.MENU_ITEM_SUFFIX}${value}"
                )
            )
        }
    }

    override fun onResume() {
        super.onResume()
        updateMenuItems()
        //after comeback from movie details chip was checked but movieAdapter displays all movies
        //1- clear check easy way
        //2- get checkedId and update (keep as data)
        fragmentStartBinding.cgTypes.clearCheck()

        /*if (fragmentStartBinding.cgTypes.checkedChipId != -1) {
            movieAdapter.searchMovieByType(
                fragmentStartBinding.cgTypes.findViewById<Chip>(
                    fragmentStartBinding.cgTypes.checkedChipId
                ).text as String
            )
        }*/
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        //outState.putInt(Constants.MOVIE_TYPES_TAB_INDEX, currentTabIndex)
    }
}