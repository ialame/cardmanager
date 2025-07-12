<template>
    <Column size="4" class="card p-1 m-1">
        <span class="mb-2 d-block">
            <Icon src="/svg/tcg/yugioh.svg" class="me-1 icon-16" />
            <span class="fw-bolder text-dark w-75">Yu-Gi-Oh!</span>
        </span>
        <RouterLink to="/cards/yugioh/sets/unsaved" class="fw-bold text-black mb-2">{{setsLinkLabel}}</RouterLink>
        <RouterLink :to="cardsLink" class="fw-bold text-black mb-2">{{cardsLinkLabel}}</RouterLink>
    </Column>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import Icon from "@components/Icon.vue";
import Column from "@components/grid/Column.vue";
import {RouterLink} from "vue-router";
import {extractedYuGiOhCardsService} from "@components/cards/yugioh/extracted/list";
import {RestComposables} from "@/vue/composables/RestComposables";
import {ExtractedPromoCardEventDTO} from "@/types";

const unsavedSets = RestComposables.useRestComposable<ExtractedPromoCardEventDTO[], []>("/api/cards/yugioh/sets/extracted", []);
const unsavedSetsCount = computed(() => unsavedSets.value?.length || 0);

const setsLinkLabel = computed(() =>  unsavedSetsCount.value > 0 ? `${unsavedSetsCount.value} extentions a ajouter en base` : 'Toutes les extensions sont en base');

const extractedCardsCount = computed(() => extractedYuGiOhCardsService.all.value.length);
const cardsLink = computed(() => extractedCardsCount.value > 0 ? "/cards/yugioh/extracted" : "/cards/yugioh/extraction");
const cardsLinkLabel = computed(() =>  extractedCardsCount.value > 0 ? `${extractedCardsCount.value} cartes en attentes` : 'Aucune carte en attente');

</script>
