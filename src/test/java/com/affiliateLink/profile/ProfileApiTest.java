package com.affiliateLink.profile;

import com.affiliateLink.profile.internal.entities.Publisher;
import com.affiliateLink.profile.internal.repositories.PublisherRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProfileApiTest {

    @Autowired MockMvc mockMvc;
    @Autowired PublisherRepository publisherRepository;

    private static final String PUBLISHER = """
            {"email":"%s","firstName":"Lina","lastName":"Benali",
             "niche":"BEAUTY","bio":"Makeup","country":"Morocco"}
            """;

    private static final String ADVERTISER = """
            {"email":"%s","companyName":"Acme","sector":"Retail","website":"https://acme.com"}
            """;

    private void createVia(String url, String body) throws Exception {
        mockMvc.perform(post(url).contentType(MediaType.APPLICATION_JSON).content(body));
    }

    @Test
    void createPublisher_returns201() throws Exception {
        mockMvc.perform(post("/api/publishers").contentType(MediaType.APPLICATION_JSON)
                        .content(PUBLISHER.formatted("lina@test.com")))
                .andExpect(status().isCreated());
    }

    @Test
    void createPublisher_invalidData_returns400() throws Exception {
        String bad = """
                {"email":"not-an-email","firstName":"","lastName":"Benali",
                 "niche":"BEAUTY","bio":"x","country":"Morocco"}
                """;
        mockMvc.perform(post("/api/publishers").contentType(MediaType.APPLICATION_JSON)
                        .content(bad))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getPublisher_unknownId_returns404() throws Exception {
        mockMvc.perform(get("/api/publishers/999999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createPublisher_duplicateEmail_returns409() throws Exception {
        createVia("/api/publishers", PUBLISHER.formatted("dup@test.com"));
        mockMvc.perform(post("/api/publishers").contentType(MediaType.APPLICATION_JSON)
                        .content(PUBLISHER.formatted("dup@test.com")))
                .andExpect(status().isConflict());
    }

    @Test
    void advertiserCannotReusePublisherEmail_returns409() throws Exception {
        createVia("/api/publishers", PUBLISHER.formatted("shared@test.com"));
        mockMvc.perform(post("/api/advertisers").contentType(MediaType.APPLICATION_JSON)
                        .content(ADVERTISER.formatted("shared@test.com")))
                .andExpect(status().isConflict());
    }

    @Test
    void changeStatus_allowed_returns204() throws Exception {
        Publisher p = publisherRepository.save(new Publisher("Sara", "Amrani",
                Publisher.Niche.GAMING, "bio", "Morocco", "sara@test.com"));
        mockMvc.perform(patch("/api/admin/profiles/" + p.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isNoContent());
    }

    @Test
    void changeStatus_notAllowed_returns409() throws Exception {
        Publisher p = publisherRepository.save(new Publisher("Sara", "Amrani",
                Publisher.Niche.GAMING, "bio", "Morocco", "sara2@test.com"));
        mockMvc.perform(patch("/api/admin/profiles/" + p.getId() + "/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"SUSPENDED\"}"))
                .andExpect(status().isConflict());
    }

    @Test
    void changeStatus_unknownId_returns404() throws Exception {
        mockMvc.perform(patch("/api/admin/profiles/999999/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"ACTIVE\"}"))
                .andExpect(status().isNotFound());
    }
}