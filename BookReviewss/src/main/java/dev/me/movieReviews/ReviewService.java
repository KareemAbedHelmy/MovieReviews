package dev.me.movieReviews;

import com.mongodb.client.result.UpdateResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

@Service
public class ReviewService {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private MongoTemplate mongoTemplate;
    public Review createReview(String reviewBody, String imdbId) {
        try {
            // Create and insert the review
            Review review = reviewRepository.insert(new Review(reviewBody));

            // Prepare the query and update objects
            Query query = new Query(Criteria.where("imdbId").is(imdbId));
            Update update = new Update().push("reviewIds", review.getId());

            // Perform the update operation
            UpdateResult result = mongoTemplate.updateFirst(query, update, Movie.class);

            // Check if the update was successful
            if (result.getMatchedCount() == 0) {
                throw new RuntimeException("Failed to find and update the movie document");
            }

            return review;
        } catch (Exception e) {
            // Log the error message for debugging purposes
            System.err.println("Error occurred while creating review: " + e.getMessage());
            e.printStackTrace();

            // Handle specific exceptions if necessary
            if (e instanceof RuntimeException) {
                throw new RuntimeException("An error occurred while updating the movie document: " + e.getMessage());
            } else {
                // Throw a generic exception if it's not a specific type
                throw new RuntimeException("An unexpected error occurred: " + e.getMessage());
            }
        }
    }
}
