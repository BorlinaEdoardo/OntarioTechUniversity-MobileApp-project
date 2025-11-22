package com.test.project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.project.database.Review
import com.test.project.database.DatabaseHelper

class ReviewAdapter(
    private val reviews: MutableList<Review>,
    private val databaseHelper: DatabaseHelper
) : RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder>() {

    class ReviewViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userNameTextView: TextView = view.findViewById(R.id.userNameTextView)
        val ratingBar: RatingBar = view.findViewById(R.id.reviewRatingBar)
        val commentTextView: TextView = view.findViewById(R.id.commentTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_review, parent, false)
        return ReviewViewHolder(view)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val review = reviews[position]

        // Get user name from database
        val userName = databaseHelper.getUserNameById(review.userId) ?: "Unknown User"
        holder.userNameTextView.text = userName

        holder.ratingBar.rating = review.rating
        holder.commentTextView.text = review.comment

        // Hide comment if empty
        if (review.comment.isEmpty()) {
            holder.commentTextView.visibility = View.GONE
        } else {
            holder.commentTextView.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int = reviews.size

    fun updateReviews(newReviews: List<Review>) {
        reviews.clear()
        reviews.addAll(newReviews)
        notifyDataSetChanged()
    }
}
