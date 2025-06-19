package com.shopsProject.management.service;

import com.shopsProject.management.Repository.WholesaleProdVariantRepository;
import com.shopsProject.management.dto.WholesaleProdDto;
import com.shopsProject.management.dto.WholesaleProdVariantDto.ProdVariantDto;
import com.shopsProject.management.validation.WholesaleProdChecker;
import com.shopsProject.management.domain.WholesaleProdVariant;
import com.shopsProject.management.domain.WholesaleProd;
import com.shopsProject.management.dto.WholesaleProdVariantDto;
import com.shopsProject.management.dto.WholesaleProdVariantDto.ProdStock;
import com.shopsProject.management.exception.CustomException;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * 도매업체 상품 재고 옵션 관리 서비스
 * 상품 재고 옵션 DB 저장 / 수정 / 삭제
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WholesaleProdVariantService {

    private final WholesaleProdVariantRepository prodVariantRepository;
    private final WholesaleProdChecker wholesaleProdChecker;

    public Page<ProdVariantDto> GetProdVariantPage(
        String userUuid,
        List<Integer> categoryIds,
        Boolean isActive,
        String keyword,
        Pageable pageable
    ) {
        Page<WholesaleProdVariant> variantPage = prodVariantRepository.findByUserAndFilter(
            userUuid, categoryIds, isActive, keyword, pageable
        );
        return variantPage.map(ProdVariantDto::from);
    }

    /**
     * 도매업체 특정 상품의 전체 재고 옵션 목록 반환
     * @param productId Long product_id
     * @return
     */
    public WholesaleProdVariantDto.GetProdVariantListResponse GetProdVariantsByProdId(Long productId, String uuid) {
        log.info("[도매 상품 옵션] 상품 유효성 검사");

        WholesaleProd prod = wholesaleProdChecker.validateAndGetProduct(productId, uuid, "[도매 상품 옵션]");

        log.info("[도매 상품 옵션] 특정 상품 재고 목록 리스트화 완료 / 요청자: {}, productId: {}", uuid, productId);

        return WholesaleProdVariantDto.GetProdVariantListResponse.builder()
            .productId(productId)
            .prodVariants( prod.getVariants().stream()
                .map(WholesaleProdVariantDto.ProdVariantItem::from)
                .toList() )
            .build();
    }

    /**
     * 도매업체 특정 상품의 재고 옵션 수정 요청 (재고 값만 수정)
     * @param productId Long product_id 수정 요청한 상품의 id
     * @param uuid 요청자의 uuid
     * @param req WholesaleProdVariantDto.ProdVariantItem : id, size, color, stock
     * @return
     */
    @Transactional
    public WholesaleProdVariantDto.GetProdVariantListResponse updateProdVariantsByProdId(Long productId, String uuid, WholesaleProdVariantDto.UpdateProdVariantListRequest req) {
        log.info("[도매 상품 옵션 재고 수정] 상품 유효성 검사 요청 / 요청자: {}, productId: {}", uuid, productId);

        List<WholesaleProdVariantDto.ProdStock> items = req.getProdStocks(); // 수정 요청된 값

        // 유효성 검사
        if (items == null || items.isEmpty()) {
            log.warn("[도매 상품 옵션 재고 수정] 옵션 값이 없음 / 요청자: {}, productId: {}", uuid, productId);
            throw new CustomException("NO_VARIANTS", "수정할 재고 옵션 값이 없습니다.", HttpStatus.BAD_REQUEST);
        }

        WholesaleProd wholesaleProd= wholesaleProdChecker.validateAndGetProduct(productId, uuid, "[도매 상품 옵션 재고 수정]");

        List<WholesaleProdVariant> prods = wholesaleProd.getVariants(); // 기존 DB에 저장된 값

        // 실제 id와 요청 id 집합이 같은지 검사
        Set<Long> itemIds = items.stream().map(WholesaleProdVariantDto.ProdStock::getId).collect(
            Collectors.toSet());
        Set<Long> prodIds = prods.stream().map(WholesaleProdVariant::getId).collect(Collectors.toSet());
        if (!itemIds.equals(prodIds)) {
            log.warn("[도매 상품 옵션 재고 수정] 옵션 id 불일치 / 요청자: {}, productId: {}", uuid, productId);
            throw new CustomException("INVALID_PRODUCT", "재고 옵션 불일치", HttpStatus.BAD_REQUEST);
        }

        // 상품 저장
        // Mapping하는 이유 -> prod를 꺼낼 때, id에 맞는 값을 재고 값을 저장하기 위해서
        Map<Long, ProdStock> itemMap =
            items.stream().collect(Collectors.toMap(WholesaleProdVariantDto.ProdStock::getId, Function.identity()));
        for (WholesaleProdVariant prod: prods) {
            WholesaleProdVariantDto.ProdStock item = itemMap.get(prod.getId());
            prod.setStock(item.getStock());
        }

        prodVariantRepository.saveAll(prods);

        log.info("[도매상품 옵션 재고 수정] 특정 상품의 재고 수정 완료 / 요청자: {}, product_id: {}", uuid, productId);

        return WholesaleProdVariantDto.GetProdVariantListResponse.builder()
            .productId(productId)
            .prodVariants( prods.stream()
                .map(WholesaleProdVariantDto.ProdVariantItem::from)
                .toList() )
            .build();
    }

    @Transactional
    public WholesaleProdVariantDto.updateProdVariantResponse updateProdVariantByVariantId(Long productId, Long variantId, Integer stock, String uuid) {
        log.info("[도매업체 상품 재고 수정 by variantId] 유효성 검사 / 요청자: {}, variantId: {}", uuid, variantId);

        WholesaleProdVariant variant = wholesaleProdChecker.validateAndGetVariant(productId, variantId, uuid, "[도매업체 상품 재고 수정 by variantId]");

        variant.setStock(stock);

        return WholesaleProdVariantDto.updateProdVariantResponse.builder()
            .productId(variant.getProduct().getId())
            .prodVariant(WholesaleProdVariantDto.ProdVariantItem.from(variant))
            .build();

    }


}
