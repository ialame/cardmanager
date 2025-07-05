<template>
    <div class="container mt-4">
        <Row>
            <PokemonIndexCard />
            <YuGiOhIndexCard />
            <OnePieceIndexCard />
        </Row>
    </div>
</template>

<script lang="ts" setup>
import {usePageTitle} from "@/vue/composables/PageComposables";
import Row from "@components/grid/Row.vue";
import PokemonIndexCard from "@components/cards/pokemon/PokemonIndexCard.vue";
import YuGiOhIndexCard from "@components/cards/yugioh/YuGiOhIndexCard.vue";
import OnePieceIndexCard from "@components/cards/onepiece/OnePieceIndexCard.vue";

usePageTitle();

</script>
