package com.example.moengageproject.view

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.moengageproject.R
import com.example.moengageproject.adapter.NewsAdapter
import com.example.moengageproject.databinding.ActivityMainBinding
import com.example.moengageproject.viewmodel.NewsViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog

class MainActivity : AppCompatActivity(), NewsAdapter.OnNewsItemEventsListener {
    private lateinit var binding: ActivityMainBinding
    private lateinit var newsViewModel: NewsViewModel
    private lateinit var newsAdapter: NewsAdapter
    private lateinit var newsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        newsViewModel = ViewModelProvider(this)[NewsViewModel::class.java]
        newsAdapter = NewsAdapter(emptyList(), this)
        newsRecyclerView = binding.rvNewsItems
        newsRecyclerView.layoutManager = LinearLayoutManager(this)
        newsRecyclerView.adapter = newsAdapter

        //Call News Fetch API
        newsViewModel.callApi()

        //Wait/Observe for the newsLiveData value to change
        newsViewModel.newsLiveData.observe(this) {
            try {
                if(it.isNotEmpty()){
                    binding.shimmer.shimmerFrameLayout.stopShimmerAnimation()
                    binding.shimmer.shimmerFrameLayout.visibility = View.GONE
                    binding.linearLayout.visibility = View.VISIBLE
                    binding.rvNewsItems.visibility = View.VISIBLE
                    newsAdapter.updateList(it)
                }
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                //pass the search query to filter
                newsAdapter.filter(p0.toString().trim())
                return false
            }
        })

        binding.searchView.setOnCloseListener {
            scrollToTop()
            false
        }

        binding.ivSort.setOnClickListener {
            showBottomSheet()
        }
    }

    private fun showBottomSheet() {
        val bottomSheetView = layoutInflater.inflate(R.layout.sort_bottom_sheet, null)

        val dialog = BottomSheetDialog(this)
        dialog.setContentView(bottomSheetView)

        val radioGroup: RadioGroup = bottomSheetView.findViewById(R.id.radioGroup)
        val btnApply: TextView = bottomSheetView.findViewById(R.id.tv_sort_btn)

        btnApply.setOnClickListener {
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId

            if (selectedRadioButtonId != -1) {
                val selectedRadioButton: RadioButton =
                    bottomSheetView.findViewById(selectedRadioButtonId)

                // Handle the selected radio button
                when (selectedRadioButton.id) {
                    R.id.radioAscending -> {
                        // Handle Ascending order
                        newsAdapter.sortAsc()
                    }

                    R.id.radioDescending -> {
                        // Handle Descending order
                        newsAdapter.sortDesc()
                    }
                }
            }

            scrollToTop()

            //dismiss after selection and apply
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun scrollToTop(){
        //scroll to top
        binding.rvNewsItems.smoothScrollToPosition(0)
    }
}