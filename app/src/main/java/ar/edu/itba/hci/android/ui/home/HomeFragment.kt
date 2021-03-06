package ar.edu.itba.hci.android.ui.home

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import ar.edu.itba.hci.android.MainApplication
import ar.edu.itba.hci.android.MainViewModel
import ar.edu.itba.hci.android.api.model.Routine
import ar.edu.itba.hci.android.databinding.FragmentHomeBinding
import ar.edu.itba.hci.android.ui.execution.ExecutionViewModel
import ar.edu.itba.hci.android.ui.execution.ExecutionViewModelFactory
import java.util.*

import android.util.TypedValue
import ar.edu.itba.hci.android.R


class HomeFragment : Fragment() {

    private val app:MainApplication by lazy {
        requireActivity().application as MainApplication
    }

    val mainModel:MainViewModel by activityViewModels()
    private val model: HomeViewModel by activityViewModels { HomeViewModelFactory(app) }
    val exModel: ExecutionViewModel by activityViewModels { ExecutionViewModelFactory(app) }
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var adapter:RoutineAdapter

    private var routines:List<Routine> = listOf()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        val bottomSheetFragment = FilterFragment()

        binding.filterBtn.setOnClickListener {
            bottomSheetFragment.show(parentFragmentManager, "BottomSheetDialog")
        }

        model.onlyFavorite.observe(viewLifecycleOwner, {
            sortAndFilter(null)
        })

        model.ordering.observe(viewLifecycleOwner, {
            sortAndFilter(null)
        })

        return binding.root
    }

    private fun sortAndFilter(search:String?) {
        val fav = model.onlyFavorite.value
        val order = model.ordering.value
        adapter.routines = routines.filter { fav == null || !fav || it.metadata.favorite == fav }
            .sortedWith(
                when(order) {
                    Ordering.DATE -> compareBy { it.date }
                    Ordering.DIFFICULTY -> compareBy { it.difficulty }
                    Ordering.SCORE -> compareByDescending { it.metadata.score }
                    Ordering.DURATION -> compareBy { it.metadata.duration }
                    else -> compareBy { it.date }
                }
            )
        search?.let { text ->
            adapter.routines = adapter.routines.filter { text.lowercase() in it.name.lowercase() }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = RoutineAdapter(this)

        binding.recycler.layoutManager = LinearLayoutManager(context)
//        binding.recycler?.layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        binding.recycler.adapter = adapter

        model.routines.observe(viewLifecycleOwner, {
            adapter.routines = it.content
            routines = it.content
        })

        (binding.search as SearchView).setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?) = false

            override fun onQueryTextChange(newText: String?): Boolean {
                sortAndFilter(newText)
                return true
            }
        })


        //Get colorPrimary
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        val colorPrimary = typedValue.data

        binding.swipeRefresh.setColorSchemeColors(colorPrimary)
        binding.swipeRefresh.isRefreshing = true
        binding.swipeRefresh.setOnRefreshListener {
            model.refreshRoutines()
        }
            
        model.routines.observe(viewLifecycleOwner, {
            adapter.routines = it.content
            binding.swipeRefresh.isRefreshing = false
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}