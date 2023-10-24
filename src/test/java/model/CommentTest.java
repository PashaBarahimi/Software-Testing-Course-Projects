package model;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.*;

public class CommentTest {
    private Comment comment;

    @BeforeEach
    public void setUp() {
        comment = new Comment(1, "misasha@gmail.com", "misasha", 1, "text");
    }

    @Test
    public void testConstructor_hasCorrectValue() {
        Assertions.assertEquals(1, comment.getId());
        Assertions.assertEquals("text", comment.getText());
        Assertions.assertEquals("misasha@gmail.com", comment.getUserEmail());
        Assertions.assertEquals("misasha", comment.getUsername());
    }

    @Test
    public void testGetCurrentDate_hasCorrectFormat() {
        String currentDate = comment.getCurrentDate();
        Assertions.assertTrue(currentDate.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"));
    }

    @Test
    public void testGetCurrentDate_hasCorrectValue() {
        String currentDate = comment.getCurrentDate();
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Assertions.assertEquals(dateFormat.format(date), currentDate.substring(0, 10));
    }

    @Test
    public void testAddUserLike_hasCorrectValue() {
        comment.addUserVote("good_person", "like");
        Assertions.assertEquals(1, comment.getLike());
    }

    @Test
    public void testAddUserDislike_hasCorrectValue() {
        comment.addUserVote("bad_person", "dislike");
        Assertions.assertEquals(1, comment.getDislike());
    }

    @Test
    public void testMultipleUsersLikeAndDislike_hasCorrectValue() {
        comment.addUserVote("good_person", "like");
        comment.addUserVote("bad_person", "dislike");
        Assertions.assertEquals(1, comment.getLike());
        Assertions.assertEquals(1, comment.getDislike());
    }

    @Test
    public void testSingleUserMultipleLike_hasCorrectValue() {
        comment.addUserVote("good_person", "like");
        comment.addUserVote("good_person", "like");
        Assertions.assertEquals(1, comment.getLike());
    }

    @Test
    public void testSingleUserMultipleDislike_hasCorrectValue() {
        comment.addUserVote("bad_person", "dislike");
        comment.addUserVote("bad_person", "dislike");
        Assertions.assertEquals(1, comment.getDislike());
    }

    @Test
    public void testChangeUserVoteFromLikeToDislike_hasCorrectValue() {
        comment.addUserVote("good_person", "like");
        comment.addUserVote("good_person", "dislike");
        Assertions.assertEquals(0, comment.getLike());
        Assertions.assertEquals(1, comment.getDislike());
    }

    @Test
    public void testChangeUserVoteFromDislikeToLike_hasCorrectValue() {
        comment.addUserVote("bad_person", "dislike");
        comment.addUserVote("bad_person", "like");
        Assertions.assertEquals(1, comment.getLike());
        Assertions.assertEquals(0, comment.getDislike());
    }
}
