package controllers;

import application.BalootApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.NotExistentCommodity;
import exceptions.NotExistentUser;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import service.Baloot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static defines.Errors.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest(classes = BalootApplication.class)
public class CommoditiesControllerApiTest {
    @MockBean
    private Baloot baloot;
    @Autowired
    private CommoditiesController commoditiesController;
    @Autowired
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        commoditiesController.setBaloot(baloot);
    }

    @Test
    @DisplayName("Test getCommodities() with empty list")
    public void testGetCommoditiesApiWithEmptyList() throws Exception {
        when(baloot.getCommodities()).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/commodities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Test getCommodities() with a list of commodities")
    public void testGetCommoditiesApi() throws Exception {
        ArrayList<Commodity> commodities = new ArrayList<>() {{
            add(new Commodity());
            add(new Commodity());
        }};
        when(baloot.getCommodities()).thenReturn(commodities);
        mockMvc.perform(get("/commodities"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    @DisplayName("Test getCommodity() with a commodity")
    public void testGetCommodityApi() throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        mockMvc.perform(get("/commodities/{id}", commodity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("Test getCommodity() with a non-existing commodity")
    public void testGetCommodityApiWithNonExistingCommodity() throws Exception {
        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());
        mockMvc.perform(get("/commodities/{id}", "1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @ParameterizedTest
    @DisplayName("Test rateCommodity() with correct data")
    @ValueSource(strings = {"1", "5", "10"})
    public void testRateCommodityApi(String rate) throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        Map<String, String> map = Map.of("rate", rate, "username", "user");
        mockMvc.perform(post("/commodities/{id}/rate", commodity.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(content().string("rate added successfully!"));
    }

    @Test
    @DisplayName("Test rateCommodity() with non-existing commodity")
    public void testRateCommodityApiWithNonExistingCommodity() throws Exception {
        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());
        Map<String, String> map = Map.of("rate", "5", "username", "user");
        mockMvc.perform(post("/commodities/{id}/rate", "1")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_COMMODITY));
    }

    @ParameterizedTest
    @DisplayName("Test rateCommodity() with wrong rate format")
    @ValueSource(strings = {"Invalid", "5.5"})
    public void testRateCommodityApiWithWrongRate(String rate) throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        Map<String, String> map = Map.of("rate", rate, "username", "user");
        mockMvc.perform(post("/commodities/{id}/rate", commodity.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @ParameterizedTest
    @DisplayName("Test rateCommodity() with wrong rate range")
    @ValueSource(strings = {"-1", "0", "11"})
    public void testRateCommodityApiWithWrongRateRange(String rate) throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        Map<String, String> map = Map.of("rate", rate, "username", "user");
        mockMvc.perform(post("/commodities/{id}/rate", commodity.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(INVALID_RATE_RANGE));
    }

    @Test
    @DisplayName("Test rateCommodity() with no rate")
    public void testRateCommodityApiWithNoRate() throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        Map<String, String> map = Map.of("username", "user");
        mockMvc.perform(post("/commodities/{id}/rate", commodity.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$").isNotEmpty());
    }

    @Test
    @DisplayName("Test addCommodityComment() with correct data")
    public void testAddCommodityCommentApi() throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        User user = new User("username", "password", "email", "date", "address");
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        when(baloot.getUserById(user.getUsername())).thenReturn(user);
        Map<String, String> map = Map.of("username", user.getUsername(), "comment", "comment");
        mockMvc.perform(post("/commodities/{id}/comment", commodity.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(content().string("comment added successfully!"));
    }

    @Test
    @DisplayName("Test addCommodityComment() with non-existing user")
    public void testAddCommodityCommentApiWithNonExistingUser() throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        when(baloot.getUserById(anyString())).thenThrow(new NotExistentUser());
        Map<String, String> map = Map.of("username", "user", "comment", "comment");
        mockMvc.perform(post("/commodities/{id}/comment", commodity.getId())
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isNotFound())
                .andExpect(content().string(NOT_EXISTENT_USER));
    }

    @Test
    @DisplayName("Test getCommodityComments() with an empty list")
    public void testGetCommodityCommentsApiWithEmptyList() throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        mockMvc.perform(get("/commodities/{id}/comment", commodity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Test getCommodityComments()")
    public void testGetCommodityCommentsApi() throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        ArrayList<Comment> comments = new ArrayList<>(List.of(
                new Comment(1, "email", "username1", 1, "comment"),
                new Comment(2, "email", "username2", 1, "comment")
        ));
        when(baloot.getCommentsForCommodity(Integer.parseInt(commodity.getId()))).thenReturn(comments);
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        mockMvc.perform(get("/commodities/{id}/comment", commodity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].text").value("comment"))
                .andExpect(jsonPath("$[0].username").value("username1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].text").value("comment"))
                .andExpect(jsonPath("$[1].username").value("username2"));
    }

    @Test
    @DisplayName("Test searchCommodities() filter by name")
    public void testSearchCommoditiesApiFilterByName() throws Exception {
        ArrayList<Commodity> commodities = new ArrayList<>(List.of(
                new Commodity() {{
                    setId("1");
                    setName("name1");
                }},
                new Commodity() {{
                    setId("2");
                    setName("name2");
                }}
        ));
        when(baloot.filterCommoditiesByName("name")).thenReturn(commodities);
        Map<String, String> map = Map.of("searchOption", "name", "searchValue", "name");
        mockMvc.perform(post("/commodities/search")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("name1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].name").value("name2"));
    }

    @Test
    @DisplayName("Test searchCommodities() filter by name empty list")
    public void testSearchCommoditiesApiFilterByNameEmptyList() throws Exception {
        ArrayList<Commodity> commodities = new ArrayList<>();
        when(baloot.filterCommoditiesByName("name1")).thenReturn(commodities);
        Map<String, String> map = Map.of("searchOption", "name", "searchValue", "name1");
        mockMvc.perform(post("/commodities/search")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Test searchCommodities() filter by category")
    public void testSearchCommoditiesApiFilterByCategory() throws Exception {
        ArrayList<Commodity> commodities = new ArrayList<>(List.of(
                new Commodity() {{
                    setId("1");
                    setCategories(new ArrayList<>(List.of("category1")));
                }},
                new Commodity() {{
                    setId("2");
                    setCategories(new ArrayList<>(List.of("category2")));
                }}
        ));
        when(baloot.filterCommoditiesByCategory("category")).thenReturn(commodities);
        Map<String, String> map = Map.of("searchOption", "category", "searchValue", "category");
        mockMvc.perform(post("/commodities/search")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].categories[0]").value("category1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].categories[0]").value("category2"));
    }

    @Test
    @DisplayName("Test searchCommodities() filter by category empty list")
    public void testSearchCommoditiesApiFilterByCategoryEmptyList() throws Exception {
        ArrayList<Commodity> commodities = new ArrayList<>();
        when(baloot.filterCommoditiesByCategory("category1")).thenReturn(commodities);
        Map<String, String> map = Map.of("searchOption", "category", "searchValue", "category");
        mockMvc.perform(post("/commodities/search")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Test searchCommodities() filter by provider")
    public void testSearchCommoditiesApiFilterByProvider() throws Exception {
        ArrayList<Commodity> commodities = new ArrayList<>(List.of(
                new Commodity() {{
                    setId("1");
                    setProviderId("provider1");
                }},
                new Commodity() {{
                    setId("2");
                    setProviderId("provider2");
                }}
        ));
        when(baloot.filterCommoditiesByProviderName("provider")).thenReturn(commodities);
        Map<String, String> map = Map.of("searchOption", "provider", "searchValue", "provider");
        mockMvc.perform(post("/commodities/search")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].providerId").value("provider1"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].providerId").value("provider2"));
    }

    @Test
    @DisplayName("Test searchCommodities() filter by provider empty list")
    public void testSearchCommoditiesApiFilterByProviderEmptyList() throws Exception {
        ArrayList<Commodity> commodities = new ArrayList<>();
        when(baloot.filterCommoditiesByProviderName("provider1")).thenReturn(commodities);
        Map<String, String> map = Map.of("searchOption", "provider", "searchValue", "provider1");
        mockMvc.perform(post("/commodities/search")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Test searchCommodities() filter by invalid option")
    public void testSearchCommoditiesApiFilterByInvalidOption() throws Exception {
        Map<String, String> map = Map.of("searchOption", "invalid", "searchValue", "value");
        mockMvc.perform(post("/commodities/search")
                .contentType("application/json")
                .content(objectMapper.writeValueAsString(map)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("Test getSuggestedCommodities()")
    public void testGetSuggestedCommoditiesApi() throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        ArrayList<Commodity> commodities = new ArrayList<>(List.of(
                new Commodity() {{
                    setId("1");
                }},
                new Commodity() {{
                    setId("2");
                }}
        ));
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        when(baloot.suggestSimilarCommodities(commodity)).thenReturn(commodities);
        mockMvc.perform(get("/commodities/{id}/suggested", commodity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[1].id").value(2));
    }

    @Test
    @DisplayName("Test getSuggestedCommodities() with non-existing commodity")
    public void testGetSuggestedCommoditiesApiWithNonExistingCommodity() throws Exception {
        when(baloot.getCommodityById(anyString())).thenThrow(new NotExistentCommodity());
        mockMvc.perform(get("/commodities/suggested"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$").doesNotExist());
    }

    @Test
    @DisplayName("Test getSuggestedCommodities() with empty list")
    public void testGetSuggestedCommoditiesApiWithEmptyList() throws Exception {
        Commodity commodity = new Commodity() {{
            setId("1");
        }};
        when(baloot.getCommodityById(commodity.getId())).thenReturn(commodity);
        when(baloot.suggestSimilarCommodities(commodity)).thenReturn(new ArrayList<>());
        mockMvc.perform(get("/commodities/{id}/suggested", commodity.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
