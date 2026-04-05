package att.b2c.segment.productoffer.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import att.b2c.segment.productoffer.dto.OfferResponse;
import att.b2c.segment.productoffer.dto.UpsertOfferRequest;
import att.b2c.segment.productoffer.error.NotFoundException;
import att.b2c.segment.productoffer.service.OfferApiService;

@ExtendWith(MockitoExtension.class)
class OfferControllerTest {

    @Mock
    private OfferApiService offerApiService;

    @InjectMocks
    private OfferController offerController;

    @Test
    void getAllOffers_returnsOffers() throws Exception {
        UUID offerId = UUID.randomUUID();
        OfferResponse dto = OfferResponse.builder().offerId(offerId).name("n1").zip("12345").products(List.of()).build();

        UUID productId = UUID.randomUUID();
        when(offerApiService.getOffers(eq(productId), eq("12345"))).thenReturn(List.of(dto));

        ResponseEntity<List<OfferResponse>> response = offerController.getAllOffers(productId, "12345");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals(offerId, response.getBody().get(0).getOfferId());
    }

    @Test
    void getOffer_whenMissing_throwsNotFound() throws Exception {
        UUID offerId = UUID.randomUUID();
        when(offerApiService.getOffer(eq(offerId))).thenThrow(new NotFoundException("Offer", offerId));

        assertThrows(NotFoundException.class, () -> offerController.getOffer(offerId));
    }

    @Test
    void upsertOffer_returnsSavedDto() throws Exception {
        UUID offerId = UUID.randomUUID();
        UpsertOfferRequest request = UpsertOfferRequest.builder().offerId(offerId).name("n1").zip("12345").build();
        OfferResponse response = OfferResponse.builder().offerId(offerId).name("n1").zip("12345").products(List.of()).build();

        when(offerApiService.upsertOffer(eq(request))).thenReturn(response);

        ResponseEntity<OfferResponse> entity = offerController.upsertOffer(request);

        assertEquals(HttpStatus.OK, entity.getStatusCode());
        assertNotNull(entity.getBody());
        assertEquals(offerId, entity.getBody().getOfferId());
    }

    @Test
    void deleteOffer_returns204() throws Exception {
        UUID offerId = UUID.randomUUID();

        ResponseEntity<Void> response = offerController.deleteOffer(offerId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(offerApiService).deleteOffer(eq(offerId));
    }
}
