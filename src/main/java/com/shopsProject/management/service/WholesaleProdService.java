package com.shopsProject.management.service;

import com.shopsProject.management.Repository.CategoryRepository;
import com.shopsProject.management.Repository.ProdImgRepository;
import com.shopsProject.management.Repository.WholesaleProdVariantRepository;
import com.shopsProject.management.Repository.WholesaleProdRepository;
import com.shopsProject.management.validation.WholesaleProdChecker;
import com.shopsProject.management.domain.Category;
import com.shopsProject.management.domain.ProdImg;
import com.shopsProject.management.domain.WholesaleProdVariant;
import com.shopsProject.management.domain.User;
import com.shopsProject.management.domain.WholesaleProd;
import com.shopsProject.management.dto.WholesaleProdDto;
import com.shopsProject.management.dto.WholesaleProdDto.CreateResponse;
import com.shopsProject.management.dto.WholesaleProdDto.ProdValue;
import com.shopsProject.management.exception.CustomException;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * 도매업체 상품 등록 서비스
 * 상품 엔티티/이미지/옵션/상세블럭(블로그형 등) DB 저장 일괄 처리
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WholesaleProdService {

    private final WholesaleProdChecker wholesaleProdChecker;
    private final WholesaleProdRepository wholesaleProdRepository;
    private final ProdImgRepository prodImgRepository;
    private final WholesaleProdVariantRepository prodVariantRepository;
    private final CategoryRepository categoryRepository;

    private final CategoryService categoryService;

    /**
     * 도매 업체 상품 등록
     * : 상품 및 옵션, 이미지, 상세블록 등
     * @param req 상품 등록 요청 DTO
     * @param wholesalerUuid 로그인한 도매업체 Uuid
     * @return 상품 등록 응답 DTO
     */
    @Transactional
    public WholesaleProdDto.CreateResponse createProduct(WholesaleProdDto.CreateRequest req, String wholesalerUuid) {
        ProdValue pv = req.getProdValue();
        log.info("[도매업체 상품등록] 도매업체: {}, 요청 상품명: {}", wholesalerUuid, pv.getProductName());

        // 카테고리 유효성 확인
        Category category = categoryRepository.findById(pv.getCategory().getId())
            .orElseThrow(() -> {
                log.warn("[도매업체 상품등록] 카테고리 없음 / 요청Id: {}", pv.getCategory().getId());
                return new CustomException("CATEGORY_NOT_FOUND", "카테고리를 찾을 수 없습니다.",
                    HttpStatus.BAD_REQUEST);
            });
        log.debug("[도매업체 상품등록] 카테고리 검증 완료: {}", category.getCategoryName());

        // 상품 Entity 생성 및 저장
        log.debug("[도매업체 상품등록] 상품 Entity 생성 : {}", pv);
        WholesaleProd prod = WholesaleProd.builder()
            .wholesaler(User.builder().uuid(wholesalerUuid).build())
            .category(category)
            .productName(pv.getProductName())
            .price((pv.getPrice() != null) ? pv.getPrice() : 0)
            .isSmplAva(pv.getIsSmplAva() != null ? pv.getIsSmplAva() : true)
            .isActive(true)
            .memo(pv.getMemo())
            .description(pv.getDescription())
            .build();

        WholesaleProd savedProd = wholesaleProdRepository.save(prod);
        log.info("[도매업체 상품등록] 상품 DB 저장 완료 / prodId: {}", savedProd.getId());

        // 이미지 저장
        if (pv.getImages() != null) {
            for (WholesaleProdDto.Image imgDto : pv.getImages()) {
                ProdImg img = ProdImg.builder()
                    .product(savedProd)
                    .img(imgDto.getImgUrl())
                    .sortOrder(imgDto.getSortOrder())
                    .build();
                prodImgRepository.save(img);
                log.debug("[도매업체 상품등록] 상품 이미지 등록 / 이미지 id : {}", img.getId());
            }
        }

        // 옵션 저장
        List<String> sizeList = splitCommaSeparated(req.getSizes());
        List<String> colorList = splitCommaSeparated(req.getColors());
        log.info(sizeList.toString());
        log.info(colorList.toString());

        for (String color : colorList) {
            for (String size : sizeList) {
                WholesaleProdVariant variant = WholesaleProdVariant.builder()
                    .product(savedProd)
                    .size(size)
                    .color(color)
                    .stock(0)
                    .build();
                prodVariantRepository.save(variant);
                log.debug("[도매업체 상품등록] 상품 옵션(컬러, 사이즈, 재고) 등록 / Variant id: {}", variant.getId());
            }
        }

        //상세 블로그도

        //응답
        WholesaleProdDto.CreateResponse response = new CreateResponse();
        response.setProductId(savedProd.getId());
        log.info("[도매업체 상품등록] 최종 상품 저장 및 응답 완료 / prodId: {}", response.getProductId());
        return response;

    }

    /**
     *
     * @param productId
     * @param uuid
     * @param type
     * @return
     */
    public WholesaleProdDto.ProdResponse getProductDetails(Long productId, String uuid, String type) {
        log.info("[도매업체 상품 디테일] 정보 전닾 / 요청자: {}, productId: {}", uuid, productId);

        WholesaleProd prod = wholesaleProdChecker.validateAndGetProduct(productId, uuid, "[도매업체 상품 디테일]");

        return WholesaleProdDto.ProdResponse.builder()
            .productId(productId)
            .prodValue(buildProdValue(prod))
            .stockOptions(buildStockOptions(prod))
            .build();

    }

    /**
     *
     * @param productId
     * @param uuid
     * @return
     */
    public WholesaleProdDto.ProdDetailsForUpdateResponse getProductDetailsForEdit(Long productId, String uuid) {
        log.info("[도매업체 상품 디테일 for Edit] 유효성 검사 / 요청자: {}, productId: {}", uuid, productId);

        WholesaleProd prod = wholesaleProdChecker.validateAndGetProduct(productId, uuid, "[도매업체 상품 디테일 for Edit]");

        return WholesaleProdDto.ProdDetailsForUpdateResponse.builder()
            .productId(productId)
            .prodValue(buildProdValue(prod))
            .stockOptions(buildStockOptions(prod))
            .categories(categoryService.getCategory())
            .build();
    }

    /**
     * 도매 업체 상품 상태 변경
     * @param productId 상태 변경 요청하는 상품id
     * @param uuid 요청자의 uuid
     * @param isActive 변경할 상품의 상태 / true : 판매중, fasle : 판매 중단
     * @return productId, isActive
     */
    @Transactional
    public WholesaleProdDto.UpdateIsActiveResponse updateIsActive(Long productId, String uuid, boolean isActive) {
        log.info("[도매업체 상품 활동 변경] 판매중/판매중단 변경 요청 / productId: {}", productId);

        WholesaleProd prod = wholesaleProdChecker.validateAndGetProduct(productId, uuid, "[도매업체 상품 활동 변경]");
        System.out.println("요청 받은 상태: " + isActive);
        System.out.println("db 상태 : " + prod.isActive());
        if (prod.isActive() == isActive) {
            log.warn("[도매업체 상품 활동 변경] 이미 요청값과 동일한 상태 / productId: {}, 현재 상태: {}", productId, isActive);
            throw  new CustomException("NOT_CHANGED", isActive ? "이미 판매중인 상품입니다." : "이미 판매 중단된 상품입니다.", HttpStatus.BAD_REQUEST);
        }

        prod.setActive(!prod.isActive());
        wholesaleProdRepository.save(prod);

        log.info("[도매업체 상품 활동 변경] 판매중/판매중단 변경 요청 완료 / productId: {}, 변경 상태: {}", productId, prod.isActive());

        return WholesaleProdDto.UpdateIsActiveResponse.builder()
            .productId(productId)
            .isActive(prod.isActive())
            .build();
    }

    @Transactional
    public void deleteProduct(Long productId, String uuid) {
        log.info("[도매업체 상품 삭제 요청] 유효성 검증 / 요청자: {}. prodcutId: {}", uuid, productId);
        WholesaleProd prod = wholesaleProdChecker.validateAndGetProduct(productId, uuid, "[도매업체 상품 삭제 요청]");

        // 판매내역 여부 확인

        wholesaleProdRepository.delete(prod);
        log.info("[도매업체 상품 삭제 요청] 완료 / 요청자: {}", uuid);
    }


    // //////////////////////////////////////////////////

    /**
     *
     * @param prod
     * @return
     */
    private WholesaleProdDto.ProdValue buildProdValue(WholesaleProd prod) {

        // 이미지
        List<WholesaleProdDto.Image> images = prod.getImages().stream()
            .map(img -> WholesaleProdDto.Image.builder()
                .id(img.getId())
                .imgUrl(img.getImg())
                .sortOrder(img.getSortOrder())
                .build())
            .toList();

        // 디테일 블록
        List<WholesaleProdDto.DetailBlock> detailBlocks = prod.getDetailBlocks().stream()
            .map(detail -> WholesaleProdDto.DetailBlock.builder()
                .id(detail.getId())
                .blockType(detail.getBlockType())
                .imgUrl(detail.getImgUrl())
                .content(detail.getContent())
                .blockOrder(detail.getBlockOrder())
                .build())
            .toList();

        WholesaleProdDto.ProdValue prodValue = WholesaleProdDto.ProdValue.builder()
            .productName(prod.getProductName())
            .category(WholesaleProdDto.Category.builder()
                .id(prod.getCategory().getId())
                .name(prod.getCategory().getCategoryName())
                .build())
            .price(prod.getPrice())
            .isSmplAva(prod.isSmplAva())
            .memo(prod.getMemo())
            .description(prod.getDescription())
            .images(images)
            .detailBlocks(detailBlocks)
            .build();

        return prodValue;
    }

    /**
     *
     * @param prod
     * @return
     */
    private List<WholesaleProdDto.StockOption> buildStockOptions(WholesaleProd prod) {

        List<WholesaleProdDto.StockOption> stockOptions = prod.getVariants().stream()
            .map(stock -> WholesaleProdDto.StockOption.builder()
                .id(stock.getId())
                .size(stock.getSize())
                .color(stock.getColor())
                .stock(stock.getStock())
                .build())
            .toList();

        return stockOptions;
    }

    /**
     * 쉼표로 구분된 문자열을 List로 변환, null/빈 값은 "기본"으로 반환
     * @param str ex.) "S, M, L"
     * @return ex) ["S", "M", "L"], ["기본"]
     */
    private List<String> splitCommaSeparated(String str) {
        if (str == null || str.isBlank()) return List.of("기본");
        return Arrays.stream(str.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .toList();
    }
}