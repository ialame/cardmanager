<template>
	<Loading :ready="history !== undefined">
        <div class="full-screen-container mt-4">
            <HistoryView v-if="history" :modelValue="history">
                <template v-slot="{modelValue: v, parents: parents}">
                    <div class="container border rounded light-bg">
                        <PokemonCardEditForm :modelValue="v" :diffSource="parents" disabled />
                    </div>
                </template>
            </HistoryView>
        </div>
	</Loading>
</template>

<script lang="ts" setup>
import {HistoryTreeDTO, PokemonCardDTO} from '@/types';
import {ref, watch} from 'vue';
import HistoryView from "@components/HistoryView.vue";
import {computedAsync} from "@vueuse/core";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRoute, useRouter} from "vue-router";
import Loading from "@components/Loading.vue";
import {PokemonCardEditForm} from "@components/cards/pokemon/edit";
import {pokemonCardService} from "@components/cards/pokemon/service";

usePageTitle("Pokémon - Historique Carte");

const route = useRoute();
const router = useRouter();
const cardId = ref<string>(route.params.cardId as string || '');

const history = computedAsync<HistoryTreeDTO<PokemonCardDTO>>(async () => {
    if (cardId.value) {
        return pokemonCardService.history(cardId.value);
    }
    return undefined;
});


watch(history, v => {
    if (v) {
        const newId = v.revision.data?.id;

        if (newId) {
            cardId.value = newId;
            router.replace(`/cards/pokemon/${newId}/history`);
        }
    }
});
</script>
