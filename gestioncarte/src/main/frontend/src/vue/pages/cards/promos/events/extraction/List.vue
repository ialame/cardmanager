<template>
    <ExtractedPokemonPromoEventList v-if="tcg == 'pokemon'" />
    <ExtractedOnePiecePromoEventList v-else-if="tcg == 'onepiece'" />
</template>

<script lang="ts" setup>
import {computed} from "vue";
import {useRoute} from "vue-router";
import ExtractedPokemonPromoEventList
    from "@components/cards/pokemon/promo/event/extraction/ExtractedPokemonPromoEventList.vue";
import ExtractedOnePiecePromoEventList
    from "@components/cards/onepiece/promo/event/extraction/ExtractedOnePiecePromoEventList.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";

const route = useRoute();
const tcg = computed(() => route.params.tcg);

usePageTitle(`Promotions sans evenement`);
</script>
