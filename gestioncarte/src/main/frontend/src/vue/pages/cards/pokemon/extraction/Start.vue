<template>
    <PokemonSetSearch v-model="set" @submit="startExtraction" class="mt-3" :sets="setsWithBulbapedia">
        <AdvancedSettings>
            <hr />
            <FilterInput v-model="filters" @submit="startExtraction()" @keyup="countCards" :count="filteredCardCount"></FilterInput>
        </AdvancedSettings>
        <FormRow>
            <Column>
                <div class="d-flex flex-row float-end">
                    <FormButton color="danger" @click="clearHistory()" class="me-2" :disabled="!set">Supprimer l'historique</FormButton>
                    <FormButton v-if="set && set.translations['us'] && !set.promo" color="secondary" class="me-2" @click="startExtraction(true)">Extraire distribution</FormButton>
                    <FormButton color="primary" @click="startExtraction()" :disabled="!set">Extraire</FormButton>
                </div>
            </Column>
        </FormRow>
    </PokemonSetSearch>
    <hr>
    <div class="container">
        <Row>
            <Column>
                <RouterLink class="fw-bold text-black" to="/cards/pokemon/extracted">{{extractedCardsCount}} cartes extraites</RouterLink>
            </Column>
            <Column>
                <div class="float-end">
                    <ClearExtractedCards class="me-2" />
                    <a :class="{'btn btn-secondary me-2': true, 'disabled': 0 === extractedCardsCount}" href="/api/cards/pokemon/extracted" :download="'extracted-pokemon-cards-' + getDateStr() + '.json'"><Icon src="/svg/download.svg" /></a>
                    <UploadButton title="Upload des cartes en json" @upload="uploadJson" />
                </div>
            </Column>
        </Row>
    </div>
    <hr v-if="running">
    <div v-show="running" class="d-flex justify-content-center container">
        <div class="w-50">
            <ProgressWatcher ref="watcher" endpoint="/api/cards/pokemon/extract/progress" @start="startProgress" @end="endProgress" />
        </div>
    </div>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import {PokemonSetDTO} from '@/types';
import AdvancedSettings from '@components/form/AdvancedSettings.vue';
import FormButton from '@components/form/FormButton.vue';
import {computed, ref, watch} from 'vue';
import FormRow from '@components/form/FormRow.vue';
import Column from '@components/grid/Column.vue';
import FilterInput from '@components/form/FilterInput.vue';
import Row from "@components/grid/Row.vue";
import {getDateStr} from '@/retriever';
import UploadButton from "@components/form/UploadButton.vue";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import Icon from "@components/Icon.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {RouterLink} from "vue-router";
import {ConfirmComposables} from "@/vue/composables/ConfirmComposables";
import {getSetName, PokemonSetSearch} from "@components/cards/pokemon/set";
import {useRaise} from "@/alert";
import {ClearExtractedCards, extractedPokemonCardsService} from "@components/cards/pokemon/extracted/list";
import {ProgressWatcher} from "@/progress";
import {useSound} from '@vueuse/sound';
import pingSfx from '@/../assets/sound/ping.mp3';

usePageTitle("Pokémon - Lancer une extraction");

const raise = useRaise();

const setsWithBulbapedia = PokemonComposables.useSetsWithBulbapedia();
const set = ref<PokemonSetDTO>();
const running = ref(false);
const watcher = ref<typeof ProgressWatcher>();
const extractedCardsCount = computed(() => extractedPokemonCardsService.all.value.length);
const filters = ref<string[]>(['']);
const filteredCardCount = ref<number>(0);
const { play } = useSound(pingSfx);

const startExtraction = async (distribution?: boolean) => {
    if (!set.value) {
        raise.warn("Veuillez sélectionner une extension.");
        return;
    }
    await rest.post(`/api/cards/pokemon/set/${set.value?.id}/extract`, {
        params: { 
            distribution: !!distribution,
            filters: filters.value.join(',')
        }
    });
    raise.success("Extraction lancée.");
    set.value = undefined;
    filters.value = [''];
    watcher.value?.start();
}
const countCards = async () => {
	if (set.value && set.value.id) {
        filteredCardCount.value = await rest.get(`/api/cards/pokemon/set/${set.value?.id}/extract/count`, { params: { filters: filters.value.join(',') } });
	}
};
const uploadJson = async (content: ArrayBuffer) => {
    const decoder = new TextDecoder("utf-8");

    await extractedPokemonCardsService.addCards(JSON.parse(decoder.decode(content)));
    raise.success('Cartes uploadées avec succès');
}
const clearHistory = async () => {
    if (!set.value) {
        raise.warn("Veuillez sélectionner une extension.");
        return;
    } else if (!await ConfirmComposables.confirmOrCancel(`Vous êtes sur le point de supprimer l'historique de l'extraction de l'extension ${getSetName(set.value)}. Continuer ?`)) {
        return;
    }
    await rest.delete(`/api/cards/pokemon/set/${set.value?.id}/extracted/history`);
    raise.success("Historique supprimé.")
}

const startProgress =() => {
    running.value = true;
}
const endProgress = () => {
    running.value = false;
    extractedPokemonCardsService.fetch();
    raise.success("Extraction terminée.");
    play();
}
watch(set, () => {
	if (set.value?.id) {
		countCards();
	}
});
</script>
