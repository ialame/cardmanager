<template>
    <Column size="4" class="card p-1 m-1">
        <span class="mb-2 d-block">
            <Icon src="/svg/tcg/pokemon.svg" class="me-1 icon-16" />
            <span class="fw-bolder text-dark w-75">Pokémon</span>
        </span>
        <RouterLink :to="extractedCardsLink" class="fw-bold text-black mb-2">{{ extractedCardsLinkLabel }}</RouterLink>
        <RouterLink to="/cards/pokemon/promos/events/extracted" class="fw-bold text-black mb-2">{{ eventsLabel }}</RouterLink>
    </Column>
</template>

<script lang="ts" setup>
import {computed} from "vue";
import Icon from "@components/Icon.vue";
import Column from "@components/grid/Column.vue";
import {RouterLink} from "vue-router";
import {extractedPokemonCardsService} from "@components/cards/pokemon/extracted/list";
import {RestComposables} from "@/vue/composables/RestComposables";
import {ExtractedPromoCardEventDTO} from "@/types";

const events = RestComposables.useRestComposable<ExtractedPromoCardEventDTO[], []>("/api/cards/promos/events/extracted?tcg=pok", []);
const eventsCount = computed(() => events.value?.length ?? 0);
const eventsLabel = computed(() => eventsCount.value > 0 ? `${eventsCount.value} promos sans évènement` : 'Aucune promo sans évènement');

const extractedCardsCount = computed(() => extractedPokemonCardsService.all.value.length);
const extractedCardsLink = computed(() => extractedCardsCount.value > 0 ? "/cards/pokemon/extracted" : "/cards/pokemon/extraction");
const extractedCardsLinkLabel = computed(() =>  extractedCardsCount.value > 0 ? `${extractedCardsCount.value} cartes en attentes` : 'Aucune carte en attente');

</script>
