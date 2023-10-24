package model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class CommentTest {
    private Comment comment;

    @BeforeEach
    public void setUp() {
        comment = new Comment(1, "misasha@gmail.com", "misasha", 1, "text");
    }

    @Test
    @DisplayName("Test comment constructor")
    public void testConstructor_hasCorrectValue() {
        Assertions.assertEquals(1, comment.getId());
        Assertions.assertEquals("text", comment.getText());
        Assertions.assertEquals("misasha@gmail.com", comment.getUserEmail());
        Assertions.assertEquals("misasha", comment.getUsername());
    }

    @Test
    @DisplayName("Test current date format")
    public void testGetCurrentDate_hasCorrectFormat() {
        String currentDate = comment.getCurrentDate();
        Assertions.assertTrue(currentDate.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    @DisplayName("Test current date value")
    public void testGetCurrentDate_hasCorrectValue() {
        String currentDate = comment.getCurrentDate();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Assertions.assertEquals(dateFormat.format(date), currentDate.substring(0, 10));
    }

    @Test
    @DisplayName("Test user like")
    public void testAddUserLike_hasCorrectValue() {
        comment.addUserVote("good_person", "like");
        Assertions.assertEquals(1, comment.getLike());
    }

    @Test
    @DisplayName("Test user dislike")
    public void testAddUserDislike_hasCorrectValue() {
        comment.addUserVote("bad_person", "dislike");
        Assertions.assertEquals(1, comment.getDislike());
    }

    @Test
    @DisplayName("Test two users one liking and the other disliking")
    public void testMultipleUsersLikeAndDislike_hasCorrectValue() {
        comment.addUserVote("good_person", "like");
        comment.addUserVote("bad_person", "dislike");
        Assertions.assertEquals(1, comment.getLike());
        Assertions.assertEquals(1, comment.getDislike());
    }

    @Test
    @DisplayName("Test giving multiple likes from a single user.")
    public void testSingleUserMultipleLike_hasCorrectValue() {
        comment.addUserVote("good_person", "like");
        comment.addUserVote("good_person", "like");
        Assertions.assertEquals(1, comment.getLike());
    }

    @Test
    @DisplayName("Test giving multiple dislikes from a single user.")
    public void testSingleUserMultipleDislike_hasCorrectValue() {
        comment.addUserVote("bad_person", "dislike");
        comment.addUserVote("bad_person", "dislike");
        Assertions.assertEquals(1, comment.getDislike());
    }

    @Test
    @DisplayName("Test changing the vote of a user from like to dislike.")
    public void testChangeUserVoteFromLikeToDislike_hasCorrectValue() {
        comment.addUserVote("good_person", "like");
        comment.addUserVote("good_person", "dislike");
        Assertions.assertEquals(0, comment.getLike());
        Assertions.assertEquals(1, comment.getDislike());
    }

    @Test
    @DisplayName("Test changing the vote of a user from dislike to like")
    public void testChangeUserVoteFromDislikeToLike_hasCorrectValue() {
        comment.addUserVote("bad_person", "dislike");
        comment.addUserVote("bad_person", "like");
        Assertions.assertEquals(1, comment.getLike());
        Assertions.assertEquals(0, comment.getDislike());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 4, 7})
    @DisplayName("Test multiple users giving multiple likes")
    public void testMultipleUserMultipleLike_hasCorrectValue(int count) {
        for (int i = 0; i < count; i++) {
            comment.addUserVote("person" + i, "like");
        }
        Assertions.assertEquals(count, comment.getLike());
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 4, 7})
    @DisplayName("Test multiple users giving multiple dislikes")
    public void testMultipleUserMultipleDislike_hasCorrectValue(int count) {
        for (int i = 0; i < count; i++) {
            comment.addUserVote("person" + i, "dislike");
        }
        Assertions.assertEquals(count, comment.getDislike());
    }

    @Test
    @DisplayName("Test multiple users giving multiple likes and dislikes")
    public void testMultipleUserMultipleLikeAndDislike_hasCorrectValue() {
        comment.addUserVote("good_person1", "like");
        comment.addUserVote("bad_person1", "dislike");
        comment.addUserVote("good_person2", "like");
        comment.addUserVote("bad_person2", "dislike");
        Assertions.assertEquals(2, comment.getLike());
        Assertions.assertEquals(2, comment.getDislike());
    }
}
