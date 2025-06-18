package com.shopsProject.management.validation;

import com.shopsProject.management.Repository.WholesaleProdRepository;
import com.shopsProject.management.domain.WholesaleProd;
import com.shopsProject.management.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class WholesaleProdChecker {

    private final WholesaleProdRepository wholesaleProdRepository;

    public WholesaleProd validateAndGetProduct(Long productId, String userUuid, String logPrefix) {
        WholesaleProd prod = IsProdExist(productId, logPrefix);
        isMatchProdUser(prod, userUuid, logPrefix);
        return prod;
    }

    private WholesaleProd IsProdExist(Long productId, String logPrefix) {
        WholesaleProd prod = wholesaleProdRepository.findById(productId)
            .orElseThrow(() -> {
                log.warn("{} 존재하지 않는 상품 / productId: {}", logPrefix, productId);
                return new CustomException("NOT_FOUND_PRODUCT", "상품을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
            });

        return prod;
    }

    private void isMatchProdUser(WholesaleProd prod, String userUuid, String logPrefix) {
        if (!prod.getWholesaler().getUuid().equals(userUuid)) {
            log.warn("{} 타 도매업체 상품 무단 조회 시도 / 요청자:{}, 상품 주인:{}", logPrefix, userUuid, prod.getWholesaler().getUuid());
            throw new CustomException("ACCESS_DENIED", "본인의 상품만 조회할 수 있습니다.", HttpStatus.FORBIDDEN);
        }
    }

}
