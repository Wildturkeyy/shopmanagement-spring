package com.shopsProject.management.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shopsProject.management.domain.QWholesaleProd;
import com.shopsProject.management.domain.QWholesaleProdVariant;
import com.shopsProject.management.domain.WholesaleProdVariant;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

public class WholesaleProdVariantRepositoryImpl implements WholesaleProdVariantRepositoryCustom{

    private final JPAQueryFactory jpaQueryFactory;

    public WholesaleProdVariantRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<WholesaleProdVariant> findByUserAndFilter(String userUuid, List<Integer> categoryIds,
        Boolean isActive, String keyword, Pageable pageable) {

        QWholesaleProdVariant variant = QWholesaleProdVariant.wholesaleProdVariant;
        QWholesaleProd product = QWholesaleProd.wholesaleProd;

        BooleanBuilder builder = new BooleanBuilder();

        // 소유 도매업체 필터
        builder.and(product.wholesaler.uuid.eq(userUuid));

        // 카테고리
        if (categoryIds != null && !categoryIds.isEmpty()) {
            builder.and(product.category.id.in((Number) categoryIds));
        }

        // 판매여부 활성화
        if (isActive != null) {
            builder.and(product.isActive.eq(isActive));
        }

        // 키워드
        if (keyword != null && !keyword.isBlank()) {
            builder.and(product.productName.containsIgnoreCase(keyword));
        }

        // 정렬....
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
        for (Sort.Order o : pageable.getSort()) {
            if (o.getProperty().equals("createdAt")) {
                orderSpecifiers.add(
                    new OrderSpecifier<>(
                        o.isAscending() ? Order.ASC : Order.DESC,
                        product.createdAt
                    )
                );
            } else if (o.getProperty().equals("productName")) {
                orderSpecifiers.add(
                    new OrderSpecifier<>(
                        o.isAscending() ? Order.ASC : Order.DESC,
                        product.productName
                    )
                );
            }
        }

        JPQLQuery<WholesaleProdVariant> query = jpaQueryFactory
            .selectFrom(variant)
            .join(variant.product, product)
            .where(builder);

        if (!orderSpecifiers.isEmpty()) {
            query.orderBy(orderSpecifiers.toArray(new OrderSpecifier[0]));
        } else {
            query.orderBy(product.createdAt.desc());
        }

        long total = query.fetchCount();
        List<WholesaleProdVariant> content = query
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize())
            .fetch();

        return new PageImpl<>(content, pageable, total);
    }
}
