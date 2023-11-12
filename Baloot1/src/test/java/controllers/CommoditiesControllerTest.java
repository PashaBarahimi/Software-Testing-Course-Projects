package controllers;

import exceptions.NotExistentCommodity;
import exceptions.NotExistentUser;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static defines.Errors.*;
import static org.mockito.Mockito.*;

public class CommoditiesControllerTest {
    private CommoditiesController commoditiesController;
    private Commodity commodity;
    private Baloot baloot;

    @BeforeEach
    public void setUp() {
        baloot = mock(Baloot.class);
        commoditiesController = new CommoditiesController();
        commoditiesController.setBaloot(baloot);
        commodity = spy(new Commodity() {{
            setId("1");
        }});
    }

    @Test
    @DisplayName("Test get commodities when empty")
    public void testGetCommoditiesEmpty() {
        when(baloot.getCommodities()).thenReturn(new ArrayList<>());
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getCommodities();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(new ArrayList<>(), response.getBody());
    }

    @Test
    @DisplayName("Test get commodities when not empty")
    public void testGetCommodities() {
        ArrayList<Commodity> commodities = new ArrayList<>(List.of(
                new Commodity() {{
                    setId("1");
                }},
                new Commodity() {{
                    setId("2");
                }},
                new Commodity() {{
                    setId("3");
                }}
        ));
        when(baloot.getCommodities()).thenReturn(commodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getCommodities();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test get existing commodity")
    public void testGetExistingCommodity() throws NotExistentCommodity {
        when(baloot.getCommodityById("1")).thenReturn(commodity);
        ResponseEntity<Commodity> response = commoditiesController.getCommodity("1");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(commodity, response.getBody());
    }

    @Test
    @DisplayName("Test get nonexistent commodity")
    public void testGetNonexistentCommodity() throws NotExistentCommodity {
        when(baloot.getCommodityById("1")).thenThrow(new NotExistentCommodity());
        ResponseEntity<Commodity> response = commoditiesController.getCommodity("1");
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertNull(response.getBody());
    }

    @Test
    @DisplayName("Test rate commodity with correct data")
    public void testRateCommodity() throws NotExistentCommodity {
        Map<String, String> input = Map.of("rate", "5", "username", "person");
        when(baloot.getCommodityById("1")).thenReturn(commodity);
        ResponseEntity<String> response = commoditiesController.rateCommodity("1", input);
        verify(commodity, times(1)).addRate("person", 5);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("rate added successfully!", response.getBody());
    }

    @Test
    @DisplayName("Test rate commodity with nonexistent commodity")
    public void testRateCommodityWithNonexistentCommodity() throws NotExistentCommodity {
        Map<String, String> input = Map.of("rate", "5", "username", "person");
        when(baloot.getCommodityById("1")).thenThrow(new NotExistentCommodity());
        ResponseEntity<String> response = commoditiesController.rateCommodity("1", input);
        verify(commodity, never()).addRate(anyString(), anyInt());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(NOT_EXISTENT_COMMODITY, response.getBody());
    }

    @Test
    @DisplayName("Test rate commodity with wrong number format")
    public void testRateCommodityWithWrongNumberFormat() throws NotExistentCommodity {
        Map<String, String> input = Map.of("rate", "5.5", "username", "person");
        when(baloot.getCommodityById("1")).thenReturn(commodity);
        ResponseEntity<String> response = commoditiesController.rateCommodity("1", input);
        verify(commodity, never()).addRate(anyString(), anyInt());
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    @DisplayName("Test rate commodity with wrong range")
    public void testRateCommodityWithWrongRange() throws NotExistentCommodity {
        Map<String, String> input = Map.of("rate", "12", "username", "person");
        when(baloot.getCommodityById("1")).thenReturn(commodity);
        ResponseEntity<String> response = commoditiesController.rateCommodity("1", input);
        verify(commodity, times(1)).addRate("person", 12);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals(INVALID_RATE_RANGE, response.getBody());
    }

    @Test
    @DisplayName("Test add comment to commodity with existing user")
    public void testAddCommodityComment() throws NotExistentUser {
        Map<String, String> input = Map.of("username", "person", "comment", "comment");
        User user = new User("person", "123", "email@mail.com", "2023", "address");
        when(baloot.getUserById("person")).thenReturn(user);
        when(baloot.generateCommentId()).thenReturn(1);
        ResponseEntity<String> response = commoditiesController.addCommodityComment("1", input);
        ArgumentCaptor<Comment> commentCaptor = ArgumentCaptor.forClass(Comment.class);
        verify(baloot, times(1)).addComment(commentCaptor.capture());
        Comment comment = commentCaptor.getValue();
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals("comment added successfully!", response.getBody());
        Assertions.assertEquals(1, comment.getId());
        Assertions.assertEquals("person", comment.getUsername());
        Assertions.assertEquals("email@mail.com", comment.getUserEmail());
        Assertions.assertEquals(1, comment.getCommodityId());
        Assertions.assertEquals("comment", comment.getText());
    }

    @Test
    @DisplayName("Test add comment to commodity with nonexistent user")
    public void testAddCommodityCommentWithNonexistentUser() throws NotExistentUser {
        Map<String, String> input = Map.of("username", "person", "comment", "comment");
        when(baloot.getUserById("person")).thenThrow(new NotExistentUser());
        ResponseEntity<String> response = commoditiesController.addCommodityComment("1", input);
        verify(baloot, never()).addComment(any(Comment.class));
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(NOT_EXISTENT_USER, response.getBody());
    }

    @Test
    @DisplayName("Test get commodity comments empty list")
    public void testGetCommodityCommentsEmpty() {
        when(baloot.getCommentsForCommodity(1)).thenReturn(new ArrayList<>());
        ResponseEntity<ArrayList<Comment>> response = commoditiesController.getCommodityComment("1");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(new ArrayList<>(), response.getBody());
    }

    @Test
    @DisplayName("Test get commodity comments not empty list")
    public void testGetCommodityComments() {
        ArrayList<Comment> comments = new ArrayList<>(List.of(
                new Comment(),
                new Comment()
        ));
        when(baloot.getCommentsForCommodity(1)).thenReturn(comments);
        ResponseEntity<ArrayList<Comment>> response = commoditiesController.getCommodityComment("1");
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(comments, response.getBody());
    }

    @Test
    @DisplayName("Test search commodity filter by name")
    public void testSearchCommodityFilterByName() {
        ArrayList<Commodity> commodities = new ArrayList<>(List.of(
                new Commodity() {{
                    setId("1");
                    setName("name1");
                }},
                new Commodity() {{
                    setId("2");
                    setName("xname2");
                }}
        ));
        when(baloot.filterCommoditiesByName("name")).thenReturn(commodities);
        Map<String, String> input = Map.of("searchOption", "name", "searchValue", "name");
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test search commodity filter by name with empty list")
    public void testSearchCommodityFilterByNameEmpty() {
        ArrayList<Commodity> commodities = new ArrayList<>();
        when(baloot.filterCommoditiesByName("name")).thenReturn(commodities);
        Map<String, String> input = Map.of("searchOption", "name", "searchValue", "name");
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test search commodity filter by category")
    public void testSearchCommodityFilterByCategory() {
        ArrayList<Commodity> commodities = new ArrayList<>(List.of(
                new Commodity() {{
                    setId("1");
                    setCategories(new ArrayList<>(List.of("category1")));
                }},
                new Commodity() {{
                    setId("2");
                    setCategories(new ArrayList<>(List.of("cat", "xcategory2")));
                }}
        ));
        when(baloot.filterCommoditiesByCategory("category")).thenReturn(commodities);
        Map<String, String> input = Map.of("searchOption", "category", "searchValue", "category");
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test search commodity filter by category with empty list")
    public void testSearchCommodityFilterByCategoryEmpty() {
        ArrayList<Commodity> commodities = new ArrayList<>();
        when(baloot.filterCommoditiesByCategory("category")).thenReturn(commodities);
        Map<String, String> input = Map.of("searchOption", "category", "searchValue", "category");
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test search commodity filter by provider")
    public void testSearchCommodityFilterByProvider() {
        ArrayList<Commodity> commodities = new ArrayList<>(List.of(
                new Commodity() {{
                    setId("1");
                    setProviderId("provider1");
                }},
                new Commodity() {{
                    setId("2");
                    setProviderId("xprovider2");
                }}
        ));
        when(baloot.filterCommoditiesByProviderName("provider")).thenReturn(commodities);
        Map<String, String> input = Map.of("searchOption", "provider", "searchValue", "provider");
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test search commodity filter by provider with empty list")
    public void testSearchCommodityFilterByProviderEmpty() {
        ArrayList<Commodity> commodities = new ArrayList<>();
        when(baloot.filterCommoditiesByProviderName("provider")).thenReturn(commodities);
        Map<String, String> input = Map.of("searchOption", "provider", "searchValue", "provider");
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test search commodity filter by invalid option")
    public void testSearchCommodityFilterByInvalidOption() {
        Map<String, String> input = Map.of("searchOption", "invalid", "searchValue", "value");
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(input);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(new ArrayList<>(), response.getBody());
    }

    @Test
    @DisplayName("Test get suggested commodities")
    public void testGetSuggestedCommodities() throws NotExistentCommodity {
        ArrayList<Commodity> commodities = new ArrayList<>(List.of(
                new Commodity(),
                new Commodity()
        ));
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        when(baloot.suggestSimilarCommodities(commodity)).thenReturn(commodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodity.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(commodities, response.getBody());
    }

    @Test
    @DisplayName("Test get suggested commodities with nonexistent commodity")
    public void testGetSuggestedCommoditiesWithNonexistentCommodity() throws NotExistentCommodity {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenThrow(new NotExistentCommodity());
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodity.getId());
        Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Assertions.assertEquals(new ArrayList<>(), response.getBody());
    }

    @Test
    @DisplayName("Test get suggested commodities with empty list")
    public void testGetSuggestedCommoditiesEmpty() throws NotExistentCommodity {
        ArrayList<Commodity> commodities = new ArrayList<>();
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        when(baloot.suggestSimilarCommodities(commodity)).thenReturn(commodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodity.getId());
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(commodities, response.getBody());
    }
}
