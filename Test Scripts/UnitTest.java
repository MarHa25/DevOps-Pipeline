package com.cardiff.comm.devops;

import com.cardiff.comm.exception.BlockCreationException;
import com.cardiff.comm.repository.BlockManagementRepository;
import com.cardiff.comm.service.BlockManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitTest {

    @Mock
    private BlockManagementRepository repository;

    @InjectMocks
    private BlockManagementService service;

    // ======================================================
    // POSITIVE TESTS
    // ======================================================

    @Test
    void updateOrganisationBlock_updatesCoreFields() {
        service.updateOrganisationBlock(
                1L,
                "New Title",
                "New Description",
                "New Address",
                null,
                null,
                null,
                null
        );

        verify(repository).updateOrganisation(1L, "New Title", "New Description");
        verify(repository).updatePanelContent(1L, "New Title", "New Description");
        verify(repository).updatePanelInfo(1L, "New Address");

        verify(repository).deletePanelLinks(1L);
        verify(repository).deletePanelPromos(1L);
    }

    @Test
    void updateOrganisationBlock_insertsInstagramWhenPresent() {
        when(repository.insertPanelLink(anyString(), anyString(), anyString()))
                .thenReturn(true);

        service.updateOrganisationBlock(
                1L,
                "Title",
                "Description",
                "Address",
                "insta_link",
                null,
                null,
                null
        );

        verify(repository).insertPanelLink("Instagram", "insta_link", "1");
    }

    @Test
    void updateOrganisationBlock_insertsFacebookWhenPresent() {
        when(repository.insertPanelLink(anyString(), anyString(), anyString()))
                .thenReturn(true);

        service.updateOrganisationBlock(
                1L,
                "Title",
                "Description",
                "Address",
                null,
                "facebook_link",
                null,
                null
        );

        verify(repository).insertPanelLink("Facebook", "facebook_link", "1");
    }

    @Test
    void updateOrganisationBlock_insertsWeeklyPromoWhenPresent() {
        when(repository.insertPanelPromo(anyString(), anyString(), anyString()))
                .thenReturn(true);

        service.updateOrganisationBlock(
                1L,
                "Title",
                "Description",
                "Address",
                null,
                null,
                "weekly deal",
                null
        );

        verify(repository).insertPanelPromo("weekly", "weekly deal", "1");
    }

    @Test
    void updateOrganisationBlock_insertsDailyPromoWhenPresent() {
        when(repository.insertPanelPromo(anyString(), anyString(), anyString()))
                .thenReturn(true);

        service.updateOrganisationBlock(
                1L,
                "Title",
                "Description",
                "Address",
                null,
                null,
                null,
                "daily deal"
        );

        verify(repository).insertPanelPromo("daily", "daily deal", "1");
    }

    // ======================================================
    // OPTIONAL FIELD DELETION TESTS
    // ======================================================

    @Test
    void updateOrganisationBlock_deletesExistingOptionalFieldsBeforeUpdate() {
        service.updateOrganisationBlock(
                1L,
                "Title",
                "Description",
                "Address",
                null,
                null,
                null,
                null
        );

        verify(repository).deletePanelLinks(1L);
        verify(repository).deletePanelPromos(1L);

        verify(repository, never()).insertPanelLink(any(), any(), any());
        verify(repository, never()).insertPanelPromo(any(), any(), any());
    }

    @Test
    void updateOrganisationBlock_removesSocialLinksButKeepsPromos() {
        when(repository.insertPanelPromo(anyString(), anyString(), anyString()))
                .thenReturn(true);

        service.updateOrganisationBlock(
                1L,
                "Title",
                "Description",
                "Address",
                null,
                null,
                "weekly deal",
                "daily deal"
        );

        verify(repository).deletePanelLinks(1L);
        verify(repository).deletePanelPromos(1L);

        verify(repository, never()).insertPanelLink(any(), any(), any());

        verify(repository).insertPanelPromo("weekly", "weekly deal", "1");
        verify(repository).insertPanelPromo("daily", "daily deal", "1");
    }

    @Test
    void updateOrganisationBlock_removesPromosButKeepsSocialLinks() {
        when(repository.insertPanelLink(anyString(), anyString(), anyString()))
                .thenReturn(true);

        service.updateOrganisationBlock(
                1L,
                "Title",
                "Description",
                "Address",
                "insta_link",
                "facebook_link",
                null,
                null
        );

        verify(repository).deletePanelLinks(1L);
        verify(repository).deletePanelPromos(1L);

        verify(repository).insertPanelLink("Instagram", "insta_link", "1");
        verify(repository).insertPanelLink("Facebook", "facebook_link", "1");

        verify(repository, never()).insertPanelPromo(any(), any(), any());
    }

    // ======================================================
    // REQUIRED FIELD VALIDATION TESTS
    // ======================================================

    @Test
    void updateOrganisationBlock_emptyTitle_doesNotUpdateRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateOrganisationBlock(
                        1L,
                        "",
                        "Valid Description",
                        "Valid Address",
                        null,
                        null,
                        null,
                        null
                )
        );

        verifyNoRepositoryUpdate();
    }

    @Test
    void updateOrganisationBlock_emptyDescription_doesNotUpdateRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateOrganisationBlock(
                        1L,
                        "Valid Title",
                        "",
                        "Valid Address",
                        null,
                        null,
                        null,
                        null
                )
        );

        verifyNoRepositoryUpdate();
    }

    @Test
    void updateOrganisationBlock_emptyAddress_doesNotUpdateRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateOrganisationBlock(
                        1L,
                        "Valid Title",
                        "Valid Description",
                        "",
                        null,
                        null,
                        null,
                        null
                )
        );

        verifyNoRepositoryUpdate();
    }

    @Test
    void updateOrganisationBlock_emptyRequiredFields_doesNotUpdateRepository() {
        assertThrows(
                IllegalArgumentException.class,
                () -> service.updateOrganisationBlock(
                        1L,
                        "",
                        "",
                        "",
                        null,
                        null,
                        null,
                        null
                )
        );

        verifyNoRepositoryUpdate();
    }

    // ======================================================
    // NEGATIVE TESTS - OPTIONAL INSERT FAILURES
    // ======================================================

    @Test
    void updateOrganisationBlock_instagramInsertFails_throwsException() {
        when(repository.insertPanelLink(anyString(), anyString(), anyString()))
                .thenReturn(false);

        assertThrows(
                BlockCreationException.class,
                () -> service.updateOrganisationBlock(
                        1L,
                        "Title",
                        "Description",
                        "Address",
                        "insta_link",
                        null,
                        null,
                        null
                )
        );
    }

    @Test
    void updateOrganisationBlock_facebookInsertFails_throwsException() {
        when(repository.insertPanelLink(anyString(), anyString(), anyString()))
                .thenReturn(false);

        assertThrows(
                BlockCreationException.class,
                () -> service.updateOrganisationBlock(
                        1L,
                        "Title",
                        "Description",
                        "Address",
                        null,
                        "facebook_link",
                        null,
                        null
                )
        );
    }

    @Test
    void updateOrganisationBlock_weeklyPromoInsertFails_throwsException() {
        when(repository.insertPanelPromo(anyString(), anyString(), anyString()))
                .thenReturn(false);

        assertThrows(
                BlockCreationException.class,
                () -> service.updateOrganisationBlock(
                        1L,
                        "Title",
                        "Description",
                        "Address",
                        null,
                        null,
                        "weekly deal",
                        null
                )
        );
    }

    @Test
    void updateOrganisationBlock_dailyPromoInsertFails_throwsException() {
        when(repository.insertPanelPromo(anyString(), anyString(), anyString()))
                .thenReturn(false);

        assertThrows(
                BlockCreationException.class,
                () -> service.updateOrganisationBlock(
                        1L,
                        "Title",
                        "Description",
                        "Address",
                        null,
                        null,
                        null,
                        "daily deal"
                )
        );
    }

    // ======================================================
    // EDGE CASE TEST
    // ======================================================

    @Test
    void updateOrganisationBlock_noOptionalFields_noInsertCalls() {
        service.updateOrganisationBlock(
                1L,
                "Title",
                "Description",
                "Address",
                null,
                null,
                null,
                null
        );

        verify(repository, never()).insertPanelLink(any(), any(), any());
        verify(repository, never()).insertPanelPromo(any(), any(), any());
    }

    // ======================================================
    // HELPER METHOD
    // ======================================================

    private void verifyNoRepositoryUpdate() {
        verify(repository, never()).updateOrganisation(anyLong(), anyString(), anyString());
        verify(repository, never()).updatePanelContent(anyLong(), anyString(), anyString());
        verify(repository, never()).updatePanelInfo(anyLong(), anyString());

        verify(repository, never()).deletePanelLinks(anyLong());
        verify(repository, never()).deletePanelPromos(anyLong());

        verify(repository, never()).insertPanelLink(any(), any(), any());
        verify(repository, never()).insertPanelPromo(any(), any(), any());
    }
}