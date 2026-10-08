package com.cardiff.comm.devops;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FullContainerMVC {

    @Autowired
    private MockMvc mockMvc;

    // ======================================================
    // LOGIN TESTS
    // ======================================================

    @Test
    void organiserLogin_validCredentials_redirectsToMainAndSetsSession() throws Exception {
        mockMvc.perform(post("/OrganiserCodeSubmission")
                        .param("organiser_username", "Org1")
                        .param("organiser_access_code", "Org12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main"))
                .andExpect(request().sessionAttribute("role", "ORGANISER"));
    }

    @Test
    void adminLogin_validCredentials_redirectsToMainAndSetsSession() throws Exception {
        mockMvc.perform(post("/AdminCodeSubmission")
                        .param("organiser_username", "admin1")
                        .param("organiser_access_code", "admin12345"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main"))
                .andExpect(request().sessionAttribute("role", "ADMIN"));
    }

    @Test
    void organiserLogin_invalidCredentials_returnsLoginPage() throws Exception {
        mockMvc.perform(post("/OrganiserCodeSubmission")
                        .param("organiser_username", "wrong")
                        .param("organiser_access_code", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("organiserLogin"));
    }

    @Test
    void adminLogin_invalidCredentials_returnsLoginPage() throws Exception {
        mockMvc.perform(post("/AdminCodeSubmission")
                        .param("organiser_username", "wrong")
                        .param("organiser_access_code", "wrong"))
                .andExpect(status().isOk())
                .andExpect(view().name("adminLogin"));
    }

    // ======================================================
    // SESSION / AUTH TESTS
    // ======================================================

    @Test
    void viewOrgBlocksOrganiser_notLoggedIn_redirectsToOrganiserLogin() throws Exception {
        mockMvc.perform(get("/viewOrgBlocks_Organiser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organiserLogin"));
    }

    @Test
    void viewOrgBlocksOrganiser_wrongRole_redirectsToOrganiserLogin() throws Exception {
        mockMvc.perform(get("/viewOrgBlocks_Organiser")
                        .sessionAttr("userId", 1L)
                        .sessionAttr("role", "RESIDENT"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organiserLogin"));
    }

    @Test
    void viewOrgBlocksOrganiser_loggedInAsOrganiser_returnsPage() throws Exception {
        mockMvc.perform(get("/viewOrgBlocks_Organiser")
                        .sessionAttr("userId", 1L)
                        .sessionAttr("role", "ORGANISER"))
                .andExpect(status().isOk())
                .andExpect(view().name("viewOrgBlocks_Organiser"))
                .andExpect(model().attributeExists("organisations"));
    }

    // ======================================================
    // POSITIVE TESTS FOR /organisation/update
    // ======================================================

    @Test
    void updateOrganisation_validInput_redirectsToViewPage() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"))
                .andExpect(flash().attribute("successMessage", "Organisation updated successfully!"));
    }

    @Test
    void updateOrganisation_withSocialMediaAndPromos_success() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff")
                        .param("instagram", "insta")
                        .param("facebook", "fb")
                        .param("weeklyPromo", "Weekly deal")
                        .param("dailyPromo", "Daily deal"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"))
                .andExpect(flash().attribute("successMessage", "Organisation updated successfully!"));
    }

    @Test
    void updateOrganisation_withoutOptionalFields_success() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Another Valid Organisation")
                        .param("description", "Another valid description")
                        .param("address", "New Address"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"));
    }

    // ======================================================
    // BOUNDARY TESTS
    // ======================================================

    @Test
    void updateOrganisation_titleAtMinimumLength_success() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Ab")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"));
    }

    @Test
    void updateOrganisation_titleAtMaximumLength_success() throws Exception {
        String title100 = "A".repeat(100);

        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", title100)
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"));
    }

    @Test
    void updateOrganisation_descriptionAtMinimumLength_success() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "1234567890")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"));
    }

    // ======================================================
    // VALIDATION TESTS
    // ======================================================

    @Test
    void updateOrganisation_emptyTitle_redirectsBackToEdit() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attribute("errorMessage", "Error: Organisation Name is required."));
    }

    @Test
    void updateOrganisation_blankTitle_redirectsBackToEdit() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "   ")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attribute("errorMessage", "Error: Organisation Name is required."));
    }

    @Test
    void updateOrganisation_titleTooShort_redirectsBackToEdit() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "A")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attribute("errorMessage", "Error: Name must be between 2 and 100 characters."));
    }

    @Test
    void updateOrganisation_titleTooLong_redirectsBackToEdit() throws Exception {
        String title101 = "A".repeat(101);

        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", title101)
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attribute("errorMessage", "Error: Name must be between 2 and 100 characters."));
    }

    @Test
    void updateOrganisation_emptyDescription_redirectsBackToEdit() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attribute("errorMessage", "Error: Description is required."));
    }

    @Test
    void updateOrganisation_blankDescription_redirectsBackToEdit() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "   ")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attribute("errorMessage", "Error: Description is required."));
    }

    @Test
    void updateOrganisation_descriptionTooShort_redirectsBackToEdit() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "short")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attribute("errorMessage", "Error: Description must be at least 10 characters."));
    }

    @Test
    void updateOrganisation_emptyAddress_redirectsBackToEdit() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attribute("errorMessage", "Error: Address is required."));
    }

    @Test
    void updateOrganisation_blankAddress_redirectsBackToEdit() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", "   "))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attribute("errorMessage", "Error: Address is required."));
    }

    // ======================================================
    // EDGE / ROBUSTNESS TESTS
    // ======================================================

    @Test
    void updateOrganisation_missingOptionalParams_stillRedirectsToSuccess() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"));
    }

    @Test
    void updateOrganisation_invalidBlockId_redirectsBackToEditWhenUpdateFails() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "99999")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection());
    }
}
