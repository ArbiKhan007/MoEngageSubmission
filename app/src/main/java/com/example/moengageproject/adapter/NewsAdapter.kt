package com.example.moengageproject.adapter

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.moengageproject.R
import com.example.moengageproject.model.Article
import com.example.moengageproject.utils.NewsDiffCallback
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.time.Instant
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.TimeZone


class NewsAdapter(
    private var originalList: List<Article>,
    private val newsItemEventsListener: OnNewsItemEventsListener
) :
    RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var filteredList: MutableList<Article> = originalList.toMutableList()

    interface OnNewsItemEventsListener {
        //implement event listeners on items
    }

    inner class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val newsTitle: TextView = itemView.findViewById(R.id.tv_news_title)
        val newsDesc: TextView = itemView.findViewById(R.id.tv_news_description)
        val newsContent: TextView = itemView.findViewById(R.id.tv_news_content)
        val newsImage: ImageView = itemView.findViewById(R.id.iv_news_image)
        val publishedAt: TextView = itemView.findViewById(R.id.tv_published_at)
        val author: TextView = itemView.findViewById(R.id.tv_author)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_item, parent, false)
        return NewsViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        try {
            val article = filteredList[position]

            holder.newsTitle.text = article.title
            holder.newsDesc.text = article.description
            holder.newsContent.text = article.content

            Glide.with(holder.itemView.context)
                .load(article.urlToImage)
                .placeholder(R.drawable.image_placeholder)
                .centerCrop()
                .into(holder.newsImage)

            // Parse the timestamp
            val formatter = DateTimeFormatter.ISO_INSTANT
            val instant = Instant.from(formatter.parse(article.publishedAt))

            // Convert to LocalDateTime if needed
            val localDateTime = LocalDateTime.ofInstant(instant, TimeZone.getDefault().toZoneId())

            // Format and display the LocalDateTime
            val outputFormatter = DateTimeFormatter.ofPattern("dd-mm-yyyy HH:mm:ss a")
            val formattedDateTime = localDateTime.format(outputFormatter)

            holder.publishedAt.text = formattedDateTime
            holder.author.text = article.author

            holder.itemView.setOnClickListener {
                val bottomSheetDialog = BottomSheetDialog(holder.itemView.context)
                bottomSheetDialog.setContentView(R.layout.news_bottom_sheet)
                val newsTitle: TextView? = bottomSheetDialog.findViewById(R.id.tv_news_title_btm)
                val newsContent: TextView? = bottomSheetDialog.findViewById(R.id.tv_news_content_btm)
                val newsDesc: TextView? = bottomSheetDialog.findViewById(R.id.tv_news_description_btm)
                val newsImage: ImageView? = bottomSheetDialog.findViewById(R.id.iv_news_image_btm)

                newsTitle?.text = article.title
                newsContent?.text = article.content
                newsDesc?.text = article.description
                if (newsImage != null) {
                    Glide.with(holder.itemView.context)
                        .load(article.urlToImage)
                        .placeholder(R.drawable.image_placeholder)
                        .centerCrop()
                        .into(newsImage)
                }

                bottomSheetDialog.show()
            }
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    }

    fun filter(query: String) {
        val newFilteredList = if (query.isEmpty()) {
            originalList.toList()
        } else {
            val lowerCaseQuery = query.lowercase()
            originalList.filter { article ->
                article.title.lowercase().contains(lowerCaseQuery)
            }
        }

        val diffCallback = NewsDiffCallback(filteredList, newFilteredList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        filteredList.clear()
        filteredList.addAll(newFilteredList)

        diffResult.dispatchUpdatesTo(this)
    }

    fun sortAsc(){
        val sortedList = originalList.sortedBy { it.publishedAt }
        updateList(sortedList)
    }

    fun sortDesc(){
        val sortedList = originalList.sortedByDescending { it.publishedAt }
        updateList(sortedList)
    }

    fun updateList(newList: List<Article>) {
        val diffCallback = NewsDiffCallback(filteredList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        originalList = newList
        filteredList.clear()
        filteredList.addAll(newList)

        diffResult.dispatchUpdatesTo(this)
    }
}