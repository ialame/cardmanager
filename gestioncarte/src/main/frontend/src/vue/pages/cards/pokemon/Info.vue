<template>
    <div v-if="card !== undefined" class="container border rounded light-bg mt-4">
        <PokemonCardEditForm v-model="card" :disabled="true"></PokemonCardEditForm>
    </div>
    <div v-else class="d-flex justify-content-center mt-2">
        <h4>Aucune carte ne pocede cette ID</h4>
    </div>
</template>

<script lang="ts" setup>
import {PokemonCardDTO} from '@/types';
import {PokemonCardEditForm} from "@components/cards/pokemon/edit";
import {ref} from 'vue';
import {computedAsync} from "@vueuse/core";
import rest from "@/rest";
import {useRoute} from "vue-router";
import {usePageTitle} from "@/vue/composables/PageComposables";

usePageTitle("Pokémon - Info Carte");

const route = useRoute();
const cardId = ref<string>(route.params.cardId as string || '');
const card = computedAsync<PokemonCardDTO>(async () => {
    if (cardId.value) {
        return rest.get(`/api/cards/pokemon/${cardId.value}`);
    }
    return undefined;
});
</script>
