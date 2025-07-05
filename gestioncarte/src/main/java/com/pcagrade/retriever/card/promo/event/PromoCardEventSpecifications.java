package com.pcagrade.retriever.card.promo.event;

import com.github.f4b6a3.ulid.Ulid;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;

public class PromoCardEventSpecifications {

    private PromoCardEventSpecifications() { }

    @Nonnull
    public static Specification<PromoCardEvent> hasName(@Nullable String name) {
        if (StringUtils.isBlank(name)) {
            return alwaysTrue();
        }

        return (root, query, builder) -> {
            query.distinct(true);
            return builder.or(builder.equal(root.get("name"), name), builder.equal(root.get("name"), PromoCardHelper.cleanPromoName(name)));
        };
    }

    @Nonnull
    public static Specification<PromoCardEvent> hasPromoInSets(@Nullable Ulid... setIds) {
        if (ArrayUtils.isEmpty(setIds)) {
            return alwaysTrue();
        }

        return (root, query, builder) -> {
            query.distinct(true);
            return root.join("promoCards")
                    .join("card")
                    .join("cardSets")
                    .get("id")
                    .in((Object[]) setIds);
        };
    }

    @Nonnull
    private static Specification<PromoCardEvent> alwaysTrue() {
        return (root, query, builder) -> builder.conjunction();
    }

}
