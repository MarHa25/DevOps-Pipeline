package com.cardiff.comm.devops;

import com.cardiff.comm.controller.BlockController;
import com.cardiff.comm.repository.BlockManagementRepository;
import com.cardiff.comm.repository.BlockRepository;
import com.cardiff.comm.service.BlockManagementService;
import com.cardiff.comm.service.BlockMovementService;
import com.cardiff.comm.service.BlockService;
import com.cardiff.comm.utils.BlockValidationUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BlockController.class)
class LightweightMockMVC {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BlockService blockService;

    @MockitoBean
    private BlockRepository blockRepository;

    @MockitoBean
    private BlockManagementRepository blockManagementRepository;

    @MockitoBean
    private BlockManagementService blockManagementService;

    @MockitoBean
    private BlockMovementService blockMovementService;

    @MockitoBean
    private BlockValidationUtils blockValidation;

    // ======================================================
    // POSITIVE TESTS
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

        verify(blockManagementService).updateOrganisationBlock(
                1L,
                "Valid Organisation",
                "Valid description text",
                "Cardiff",
                null,
                null,
                null,
                null
        );
    }

    @Test
    void updateOrganisation_withOptionalFields_callsServiceWithAllValues() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff")
                        .param("instagram", "insta_link")
                        .param("facebook", "fb_link")
                        .param("weeklyPromo", "Weekly deal")
                        .param("dailyPromo", "Daily deal"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"))
                .andExpect(flash().attribute("successMessage", "Organisation updated successfully!"));

        verify(blockManagementService).updateOrganisationBlock(
                1L,
                "Valid Organisation",
                "Valid description text",
                "Cardiff",
                "insta_link",
                "fb_link",
                "Weekly deal",
                "Daily deal"
        );
    }

    @Test
    void updateOrganisation_titleAtMinimumBoundary_allowsUpdate() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Ab")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"));

        verify(blockManagementService).updateOrganisationBlock(
                1L,
                "Ab",
                "Valid description text",
                "Cardiff",
                null,
                null,
                null,
                null
        );
    }

    @Test
    void updateOrganisation_descriptionAtMinimumBoundary_allowsUpdate() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "1234567890")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"));

        verify(blockManagementService).updateOrganisationBlock(
                1L,
                "Valid Organisation",
                "1234567890",
                "Cardiff",
                null,
                null,
                null,
                null
        );
    }

    @Test
    void updateOrganisation_titleAtMaximumBoundary_allowsUpdate() throws Exception {
        String maxTitle = "A".repeat(100);

        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", maxTitle)
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"));

        verify(blockManagementService).updateOrganisationBlock(
                1L,
                maxTitle,
                "Valid description text",
                "Cardiff",
                null,
                null,
                null,
                null
        );
    }

    // ======================================================
    // OPTIONAL FIELD DELETION TESTS
    // ======================================================

    @Test
    void updateOrganisation_emptyOptionalFields_stillUpdatesSuccessfully() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff")
                        .param("instagram", "")
                        .param("facebook", "")
                        .param("weeklyPromo", "")
                        .param("dailyPromo", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"))
                .andExpect(flash().attribute("successMessage", "Organisation updated successfully!"));

        verify(blockManagementService).updateOrganisationBlock(
                1L,
                "Valid Organisation",
                "Valid description text",
                "Cardiff",
                "",
                "",
                "",
                ""
        );
    }

    @Test
    void updateOrganisation_removesSocialLinksButKeepsPromos() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff")
                        .param("instagram", "")
                        .param("facebook", "")
                        .param("weeklyPromo", "Weekly deal")
                        .param("dailyPromo", "Daily deal"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"))
                .andExpect(flash().attribute("successMessage", "Organisation updated successfully!"));

        verify(blockManagementService).updateOrganisationBlock(
                1L,
                "Valid Organisation",
                "Valid description text",
                "Cardiff",
                "",
                "",
                "Weekly deal",
                "Daily deal"
        );
    }

    @Test
    void updateOrganisation_removesPromosButKeepsSocialLinks() throws Exception {
        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff")
                        .param("instagram", "insta_link")
                        .param("facebook", "fb_link")
                        .param("weeklyPromo", "")
                        .param("dailyPromo", ""))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/view/1"))
                .andExpect(flash().attribute("successMessage", "Organisation updated successfully!"));

        verify(blockManagementService).updateOrganisationBlock(
                1L,
                "Valid Organisation",
                "Valid description text",
                "Cardiff",
                "insta_link",
                "fb_link",
                "",
                ""
        );
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

        verify(blockManagementService, never()).updateOrganisationBlock(
                anyLong(), anyString(), anyString(), anyString(), any(), any(), any(), any()
        );
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

        verify(blockManagementService, never()).updateOrganisationBlock(
                anyLong(), anyString(), anyString(), anyString(), any(), any(), any(), any()
        );
    }

    @Test
    void updateOrganisation_titleTooLong_redirectsBackToEdit() throws Exception {
        String tooLongTitle = "A".repeat(101);

        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", tooLongTitle)
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attribute("errorMessage", "Error: Name must be between 2 and 100 characters."));

        verify(blockManagementService, never()).updateOrganisationBlock(
                anyLong(), anyString(), anyString(), anyString(), any(), any(), any(), any()
        );
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

        verify(blockManagementService, never()).updateOrganisationBlock(
                anyLong(), anyString(), anyString(), anyString(), any(), any(), any(), any()
        );
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

        verify(blockManagementService, never()).updateOrganisationBlock(
                anyLong(), anyString(), anyString(), anyString(), any(), any(), any(), any()
        );
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

        verify(blockManagementService, never()).updateOrganisationBlock(
                anyLong(), anyString(), anyString(), anyString(), any(), any(), any(), any()
        );
    }

    // ======================================================
    // SERVICE EXCEPTION TEST
    // ======================================================

    @Test
    void updateOrganisation_serviceThrowsException_redirectsBackToEdit() throws Exception {
        doThrow(new RuntimeException("Database error"))
                .when(blockManagementService)
                .updateOrganisationBlock(
                        anyLong(),
                        anyString(),
                        anyString(),
                        anyString(),
                        any(),
                        any(),
                        any(),
                        any()
                );

        mockMvc.perform(post("/organisation/update")
                        .param("blockId", "1")
                        .param("title", "Valid Organisation")
                        .param("description", "Valid description text")
                        .param("address", "Cardiff"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/organisation/edit/1"))
                .andExpect(flash().attributeExists("errorMessage"));
    }
}