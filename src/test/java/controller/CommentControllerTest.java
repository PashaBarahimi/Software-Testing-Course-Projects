package controller;

import controllers.CommentController;
import exceptions.NotExistentComment;
import model.Comment;

import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.Map;

import static defines.Errors.NOT_EXISTENT_COMMENT;
import static org.mockito.Mockito.*;

public class CommentControllerTest {
    private CommentController commentController;
    private Baloot baloot;
    private Comment comment;
    private Map<String, String> input;
    private final String username = "person";

    @BeforeEach
    void setUp() {
        baloot = mock(Baloot.class);
        comment = spy(new Comment(1, "email@mail.com", username, 1, "text"));
        input = Map.of("username", username);
        commentController = new CommentController();
        commentController.setBaloot(baloot);
    }

    @Test
    @DisplayName("Test liking an existing comment")
    void testLikeComment() throws NotExistentComment {
        when(baloot.getCommentById(1)).thenReturn(comment);
        ResponseEntity<String> response = commentController.likeComment("1", input);
        verify(comment, times(1)).addUserVote(username, "like");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("The comment was successfully liked!", response.getBody());
    }

    @Test
    @DisplayName("Test liking a nonexistent comment")
    void testLikeCommentWithNonexistentComment() throws NotExistentComment {
        when(baloot.getCommentById(1)).thenThrow(new NotExistentComment());
        ResponseEntity<String> response = commentController.likeComment("1", input);
        verify(comment, times(0)).addUserVote(username, "like");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(NOT_EXISTENT_COMMENT, response.getBody());
    }

    @Test
    @DisplayName("Test liking without username")
    void testLikeCommentWithoutUsername() throws NotExistentComment {
        when(baloot.getCommentById(1)).thenReturn(comment);
        ResponseEntity<String> response = commentController.likeComment("1", Map.of());
        verify(comment, times(0)).addUserVote(username, "like");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("username not supplied.", response.getBody());
    }

    @Test
    @DisplayName("Test disliking an existing comment")
    void testDislikeComment() throws NotExistentComment {
        when(baloot.getCommentById(1)).thenReturn(comment);
        ResponseEntity<String> response = commentController.dislikeComment("1", input);
        verify(comment, times(1)).addUserVote(username, "dislike");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("The comment was successfully disliked!", response.getBody());
    }

    @Test
    @DisplayName("Test disliking a nonexistent comment")
    void testDislikeCommentWithNonexistentComment() throws NotExistentComment {
        when(baloot.getCommentById(1)).thenThrow(new NotExistentComment());
        ResponseEntity<String> response = commentController.dislikeComment("1", input);
        verify(comment, times(0)).addUserVote(username, "dislike");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(NOT_EXISTENT_COMMENT, response.getBody());
    }

    @Test
    @DisplayName("Test disliking without username")
    void testDislikeCommentWithoutUsername() throws NotExistentComment {
        when(baloot.getCommentById(1)).thenReturn(comment);
        ResponseEntity<String> response = commentController.dislikeComment("1", Map.of());
        verify(comment, times(0)).addUserVote(username, "dislike");
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("username not supplied.", response.getBody());
    }
}
