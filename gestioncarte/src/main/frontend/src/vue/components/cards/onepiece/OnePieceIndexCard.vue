<template>
    <Column size="4" class="card p-1 m-1">
        <span class="mb-2 d-block">
            <Icon src="/svg/tcg/onepiece.svg" class="me-1 icon-16" />
            <span class="fw-bolder text-dark w-75">One Piece</span>
        </span>
        <RouterLink to="/cards/onepiece/sets/unsaved" class="fw-bold text-black mb-2">{{setsLinkLabel}}</RouterLink>
        <RouterLink :to="cardsLink" class="fw-bold text-black mb-2">{{cardsLinkLabel}}</RouterLink>
        <RouterLink to="/cards/pokemon/promos/events/extracted" class="fw-bold text-black mb-2">{{ eventsLabel }}</RouterLink>
    </Column>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import Icon from "@components/Icon.vue";
import Column from "@components/grid/Column.vue";
import {RouterLink} from "vue-router";
import {useUnsavedSets} from "@components/cards/onepiece/set";
import {extractedOnePieceCardsService} from "@components/cards/onepiece/extraction/list";
import {RestComposables} from "@/vue/composables/RestComposables";
import {ExtractedPromoCardEventDTO} from "@/types";

const { state: unsavedSets } = useUnsavedSets();
const unsavedSetsCount = computed(() => unsavedSets.value?.length || 0);

const events = RestComposables.useRestComposable<ExtractedPromoCardEventDTO[], []>("/api/cards/promos/events/extracted?tcg=onp", []);
const eventsCount = computed(() => events.value?.length ?? 0);
const eventsLabel = computed(() => eventsCount.value > 0 ? `${eventsCount.value} promos sans évènement` : 'Aucune promo sans évènement');

const setsLinkLabel = computed(() =>  unsavedSetsCount.value > 0 ? `${unsavedSetsCount.value} extentions a ajouter en base` : 'Toutes les extensions sont en base');

const extractedCardsCount = computed(() => extractedOnePieceCardsService.all.value.length);
const cardsLink = computed(() => extractedCardsCount.value > 0 ? "/cards/onepiece/extracted" : "/cards/onepiece/extraction");
const cardsLinkLabel = computed(() =>  extractedCardsCount.value > 0 ? `${extractedCardsCount.value} cartes en attentes` : 'Aucune carte en attente');

</script>
