package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a Post from JSONPlaceholder API.
 *
 * 📚 LEARNING NOTES:
 * - @Data (Lombok): Auto-generates getters, setters, toString, equals, hashCode
 * - @Builder (Lombok): Enables builder pattern → Post.builder().title("Hello").build()
 * - @JsonIgnoreProperties: Ignores unknown JSON fields during deserialization
 * - REST Assured can auto-deserialize JSON responses into these objects
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Post {

    private Integer id;
    private Integer userId;
    private String title;
    private String body;
}
