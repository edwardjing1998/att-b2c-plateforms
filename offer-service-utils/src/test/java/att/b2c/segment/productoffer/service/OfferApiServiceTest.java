package att.b2c.segment.productoffer.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import att.b2c.segment.productoffer.Offer;
import att.b2c.segment.productoffer.Product;
import att.b2c.segment.productoffer.dto.OfferResponse;
import att.b2c.segment.productoffer.dto.ProductSummaryDto;
import att.b2c.segment.productoffer.error.NotFoundException;
import att.b2c.segment.productoffer.mapping.OfferMapper;
import att.b2c.segment.productoffer.mapping.ProductSummaryMapper;

@ExtendWith(MockitoExtension.class)
class OfferApiServiceTest {

    @Mock
    private OfferService offerService;

    @Mock
    private OfferMapper offerMapper;

    @Mock
    private ProductSummaryMapper productSummaryMapper;

    @InjectMocks
    private OfferApiService offerApiService;

    @Test
    void getOffers_withProductIdAndZip_usesFindByProductIdAndZip_andEnrichesProducts() {
        UUID offerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();

        Offer offer = new Offer(offerId, "o1", null, "12345");
        when(offerService.findByProductIdAndZip(eq(productId), eq("12345"))).thenReturn(List.of(offer));

        OfferResponse offerDto = OfferResponse.builder().offerId(offerId).name("o1").zip("12345").build();
        when(offerMapper.toResponse(eq(offer))).thenReturn(offerDto);

        Product p = new Product(UUID.randomUUID(), "p1", "d1", BigDecimal.TEN);
        when(offerService.findProductsByOfferIds(anyList())).thenReturn(Map.of(offerId, List.of(p)));

        ProductSummaryDto ps = ProductSummaryDto.builder().productId(p.getProductId()).name("p1").description("d1").price(BigDecimal.TEN).build();
        when(productSummaryMapper.toDto(eq(p))).thenReturn(ps);

        List<OfferResponse> result = offerApiService.getOffers(productId, "12345");

        assertEquals(1, result.size());
        assertEquals(offerId, result.get(0).getOfferId());
        assertNotNull(result.get(0).getProducts());
        assertEquals(1, result.get(0).getProducts().size());
        assertEquals(p.getProductId(), result.get(0).getProducts().get(0).getProductId());

        verify(offerService).findByProductIdAndZip(eq(productId), eq("12345"));
        verify(offerService).findProductsByOfferIds(anyList());
    }

    @Test
    void getOffer_whenMissing_throwsNotFound() {
        UUID offerId = UUID.randomUUID();
        when(offerService.findById(eq(offerId))).thenReturn(null);

        assertThrows(NotFoundException.class, () -> offerApiService.getOffer(offerId));
    }
}
