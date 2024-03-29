package com.estore.api.estoreapi.model;

import java.util.logging.Logger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;


/**
 * This class is made to represent a review
 * 
 * @author Priyank Patel
 */

@JsonPropertyOrder({"reviewId", "userId", "sku", "rating", "comment"})
public class Review {
    private static final Logger LOG = Logger.getLogger(Review.class.getName());

    private final String STRING_FORMAT = "Review [reviewId=%d, userId=%d, sku=%d, rating=%d, comment=%s]";

    private final int MIN_RATING = 1;
    private final int MAX_RATING = 5;

    
    @JsonProperty("reviewId") private int reviewId;
    @JsonProperty("userId") private int userId;
    @JsonProperty("sku") private int sku;
    @JsonProperty("rating") private int rating;
    @JsonProperty("comment") private String comment;
    
    /**
     * Create a Review with the given userID, sku, rating and review
     * @param reviewId The review ID of the review
     * @param userId The User ID of the user
     * @param sku The Product ID of the product
     * @param rating The rating of the product
     * @param comment The review of the product
     */
    @JsonCreator
    public Review(
        @JsonProperty("reviewId") int reviewId,
        @JsonProperty("userId") int userId,
        @JsonProperty("sku") int sku,
        @JsonProperty("rating") int rating,
        @JsonProperty("comment") String comment
    ) {
        //TODO: Validate the rating
        this.reviewId = reviewId;
        this.userId = userId;
        this.sku = sku;
        this.rating = rating;
        this.comment = (comment==null) ? "" : comment;
    }
    
    /**
     * Create a Review with the given userID, sku, rating, without 
     * comment
     * @param reviewId The review ID of the review
     * @param userId The User ID of the user
     * @param sku The Product ID of the product
     * @param rating The rating of the product
     */
    public Review(
        @JsonProperty("reviewId") int reviewId,
        @JsonProperty("userId") int userId,
        @JsonProperty("sku") int sku,
        @JsonProperty("rating") int rating
    ){
        this.reviewId = reviewId;
        this.userId = userId;
        this.sku = sku;
        this.rating = rating;
    }

    /**
     * Get the review ID of the review
     * @return The review ID of the review
     */
    public int getReviewId() {
        return reviewId;
    }

    /**
     * Get the User ID of the review
     * @return The User ID of the review
     */
    public int getUserId() {
        return userId;
    }

    /**
     * Set the User ID of the review
     * @param userId The User ID of the review
     */
    public void setUserId(int userId) {
        this.userId = userId;
    }

    /**
     * Get the sku of the product 
     * @return sku of the review
     */
    public int getSku() {
        return sku;
    }

    /**
     * Get the sku of the review
     * @param sku
     */
    public void setSku(int sku) {
        this.sku = sku;
    }

    /**
     * Get the rating of the review
     * @return The rating of the review
     */
    public int getRating() {
        return rating;
    }

    /**
     * Set the rating of the review
     * @param rating The rating of the review
     */
    public void setRating(int rating) {
        if( rating > MAX_RATING || rating < MIN_RATING)
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        this.rating = rating;
    }

    /**
     * Get the review comment of the review
     * @return The comment of the review
     */
    public String getComment() {
        return comment;
    }

    /**
     * Set the review comment of the review
     * @param comment The comment of the review
     */
    public void setComment(String comment) {
        if( comment == null || comment.isEmpty() )
            throw new IllegalArgumentException("Comment cannot be empty");
        this.comment = comment;
    }
        
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return String.format(STRING_FORMAT, reviewId, userId, sku, rating, comment);
    }
}
