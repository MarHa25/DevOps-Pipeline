package com.cardiff.comm.devops;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class FullContainerServlet {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    private HttpEntity<String> formRequest(String body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return new HttpEntity<>(body, headers);
    }

    // ======================================================
    // LOGIN PAGE TESTS
    // ======================================================

    @Test
    void organiserLoginPage_loadsSuccessfully() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(url("/organiser_login"), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void adminLoginPage_loadsSuccessfully() {
        ResponseEntity<String> response =
                restTemplate.getForEntity(url("/admin_login"), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void organiserLogin_validCredentials_returnsSuccessfulResponse() {
        String body =
                "organiser_username=Org1" +
                        "&organiser_access_code=Org12345";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/OrganiserCodeSubmission"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()
                || response.getStatusCode().is3xxRedirection()).isTrue();
    }

    @Test
    void adminLogin_validCredentials_returnsSuccessfulResponse() {
        String body =
                "organiser_username=admin1" +
                        "&organiser_access_code=admin12345";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/AdminCodeSubmission"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()
                || response.getStatusCode().is3xxRedirection()).isTrue();
    }

    @Test
    void organiserLogin_invalidCredentials_returnsLoginPageOrError() {
        String body =
                "organiser_username=wrong" +
                        "&organiser_access_code=wrong";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/OrganiserCodeSubmission"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void adminLogin_invalidCredentials_returnsLoginPageOrError() {
        String body =
                "organiser_username=wrong" +
                        "&organiser_access_code=wrong";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/AdminCodeSubmission"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    // ======================================================
    // POSITIVE TESTS
    // ======================================================

    @Test
    void updateOrganisation_validInput_returnsSuccessPage() {
        String body =
                "blockId=1" +
                        "&title=Updated Organisation" +
                        "&description=Valid description text" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void updateOrganisation_withPromosAndLinks_returnsSuccessPage() {
        String body =
                "blockId=1" +
                        "&title=Valid Organisation" +
                        "&description=Valid description text" +
                        "&address=Cardiff" +
                        "&instagram=testInsta" +
                        "&facebook=testFB" +
                        "&weeklyPromo=WeeklyDeal" +
                        "&dailyPromo=DailyDeal";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void updateOrganisation_withoutOptionalFields_returnsSuccessPage() {
        String body =
                "blockId=1" +
                        "&title=Another Valid Organisation" +
                        "&description=Another valid description" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    // ======================================================
    // VALIDATION TESTS
    // ======================================================

    @Test
    void updateOrganisation_emptyTitle_returnsEditPage() {
        String body =
                "blockId=1" +
                        "&title=" +
                        "&description=Valid description text" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Error: Organisation Name is required.");
    }

    @Test
    void updateOrganisation_blankTitle_returnsEditPage() {
        String body =
                "blockId=1" +
                        "&title=   " +
                        "&description=Valid description text" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Error: Organisation Name is required.");
    }

    @Test
    void updateOrganisation_titleTooShort_returnsEditPage() {
        String body =
                "blockId=1" +
                        "&title=A" +
                        "&description=Valid description text" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Error: Name must be between 2 and 100 characters.");
    }

    @Test
    void updateOrganisation_titleTooLong_returnsEditPage() {
        String longTitle = "A".repeat(101);

        String body =
                "blockId=1" +
                        "&title=" + longTitle +
                        "&description=Valid description text" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Error: Name must be between 2 and 100 characters.");
    }

    @Test
    void updateOrganisation_emptyDescription_returnsEditPage() {
        String body =
                "blockId=1" +
                        "&title=Valid Organisation" +
                        "&description=" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Error: Description is required.");
    }

    @Test
    void updateOrganisation_blankDescription_returnsEditPage() {
        String body =
                "blockId=1" +
                        "&title=Valid Organisation" +
                        "&description=   " +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Error: Description is required.");
    }

    @Test
    void updateOrganisation_descriptionTooShort_returnsEditPage() {
        String body =
                "blockId=1" +
                        "&title=Valid Organisation" +
                        "&description=short" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Error: Description must be at least 10 characters.");
    }

    @Test
    void updateOrganisation_emptyAddress_returnsEditPage() {
        String body =
                "blockId=1" +
                        "&title=Valid Organisation" +
                        "&description=Valid description text" +
                        "&address=";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Error: Address is required.");
    }

    @Test
    void updateOrganisation_blankAddress_returnsEditPage() {
        String body =
                "blockId=1" +
                        "&title=Valid Organisation" +
                        "&description=Valid description text" +
                        "&address=   ";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(response.getBody()).contains("Error: Address is required.");
    }

    // ======================================================
    // BOUNDARY TESTS
    // ======================================================

    @Test
    void updateOrganisation_titleAtMinimumLength_isAccepted() {
        String body =
                "blockId=1" +
                        "&title=Ab" +
                        "&description=Valid description text" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void updateOrganisation_titleAtMaximumLength_isAccepted() {
        String title100 = "A".repeat(100);

        String body =
                "blockId=1" +
                        "&title=" + title100 +
                        "&description=Valid description text" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void updateOrganisation_descriptionAtMinimumLength_isAccepted() {
        String body =
                "blockId=1" +
                        "&title=Valid Organisation" +
                        "&description=1234567890" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    @Test
    void updateOrganisation_largeInput_isHandledCorrectly() {
        String longText = "A".repeat(500);

        String body =
                "blockId=1" +
                        "&title=" + longText +
                        "&description=" + longText +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

    // ======================================================
    // ROBUSTNESS / SECURITY
    // ======================================================

    @Test
    void updateOrganisation_sqlInjectionAttempt_isHandledSafely() {
        String body =
                "blockId=1" +
                        "&title='; DROP TABLE organisations; --" +
                        "&description=Valid description text" +
                        "&address=Cardiff";

        ResponseEntity<String> response =
                restTemplate.postForEntity(url("/organisation/update"), formRequest(body), String.class);

        assertThat(response.getStatusCode().is2xxSuccessful()).isTrue();
    }

}