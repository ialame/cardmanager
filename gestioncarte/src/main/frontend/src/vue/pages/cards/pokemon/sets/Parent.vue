<template>
    <div class="container mt-4">
        <AdvancedFormTabManager>
            <AdvancedFormTabButton tab="sets"><Icon name="layers-outline" /></AdvancedFormTabButton>
            <AdvancedFormTabButton tab="parent"><Icon src="/svg/parent.svg" /></AdvancedFormTabButton>
        </AdvancedFormTabManager>
        <AdvancedFormTab tab="sets">
            <FormRow>
                <Column class="form-group mb-3">
                    <label class="form-label">Langue</label>
                    <LocalizationSelect v-model="localization" :localizations="['us', 'jp']" />
                </Column>
                <Column size="sm" class="form-group">
                    <label class="form-label">Serie</label>
                    <PokemonSerieSelect v-model="serie" :values="displaySeries" />
                </Column>
            </FormRow>
            <ListInput v-if="serie" label="Extensions" v-model="sets" v-slot="{item: item, first: first}" :useSeparators="true">
                <PokemonSetSearch v-model="item.value" class="card" :class="{'mt-2': !first}" :sets="computedSets" :series="computedSeries" :localization="localization" />
            </ListInput>
            <div class="d-flex justify-content-end mb-2 me-1 mt-1">
                <FormButton v-if="setIds.length > 0" color="info" @click="goToNextTab" class="ms-2">Suivant <Icon name="chevron-forward" /></FormButton>
            </div>
        </AdvancedFormTab>
        <AdvancedFormTab tab="parent">
            <PokemonSetEditForm v-model="parentSet" isParent @save="save" :localizations="localizations" />
        </AdvancedFormTab>
    </div>
</template>

<script lang="ts" setup>
import {PokemonSerieDTO, PokemonSetDTO} from '@/types';
import {computed, ref, watch, watchEffect} from 'vue';
import ListInput from "@components/form/list/ListInput.vue";
import {PokemonSetService} from "@/services/card/pokemon/set/PokemonSetService";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import LocalizationSelect from "@components/form/LocalizationSelect.vue";
import Column from "@components/grid/Column.vue";
import FormButton from "@components/form/FormButton.vue";
import FormRow from "@components/form/FormRow.vue";
import Icon from "@components/Icon.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {LocalizationCode} from "@/localization";
import {PokemonSetEditForm, PokemonSetSearch, useDisplaySeries} from "@components/cards/pokemon/set";
import {PokemonSerieSelect} from "@components/cards/pokemon/serie";
import {
    AdvancedFormTab,
    AdvancedFormTabButton,
    AdvancedFormTabManager,
    useProvideAdvancedFormTabControl
} from "@components/form/advanced";
import {uniq} from "lodash";
import {useRouter} from "vue-router";
import {useRaise} from "@/alert";
import pokemonSetService = PokemonComposables.pokemonSetService;

usePageTitle("Pokémon - Ajout d'une extension mère");

const router = useRouter();
const raise = useRaise();
const independentSets = PokemonComposables.useIndependentSets();
const localization = ref<LocalizationCode>('us');
const sets = ref<PokemonSetDTO[]>([]);
const setIds = computed(() => [...new Set(sets.value.map(set => set.id).filter(id => id !== undefined && id !== ''))]);
const parentSet = ref<PokemonSetDTO>(PokemonSetService.newSet());

const {goToNextTab} = useProvideAdvancedFormTabControl(() => ({ 'parent': !!serie.value && setIds.value.length > 0 }));

const save = async () => await pokemonSetService.batch(async service => {
    const p = await service.save(parentSet.value);

    parentSet.value.serieId = serie.value?.id || '';
    for (const s of sets.value) {
        s.parentId = p.id;
        await service.save(s);
    }
    await router.push(`/cards/pokemon/sets/${p.id}`);
    raise.success("Extension enregistrée avec succès");
});

const serie = ref<PokemonSerieDTO>();
const displaySeries = useDisplaySeries(s => s?.translations?.[localization.value] !== undefined);
const computedSeries = computed(() => !serie.value ? [] : [serie.value]);
const computedSets = computed(() => independentSets.value.filter(s => s.serieId === serie.value?.id));

const localizations = computed<LocalizationCode[]>(() => {
    const values : LocalizationCode[] = [localization.value];

    for (const s of sets.value) {
        if (s?.translations) {
            for (const [k, v] of Object.entries(s.translations)) {
                if (v && v.available) {
                    values.push(k as LocalizationCode);
                }
            }
        }
    }
    if (serie.value) {
        for (const [k, v] of Object.entries(serie.value.translations)) {
            if (v) {
                values.push(k as LocalizationCode);
            }
        }
    }
    return uniq(values);
});

watchEffect(() => {
    if (sets.value.length > 0) {
        const releaseDate = sets.value[0].translations?.[localization.value]?.releaseDate;

        if (releaseDate) {
            Object.values(parentSet.value.translations).forEach(t => t.releaseDate = releaseDate);
        }
    }
});
watch(serie, s => parentSet.value.serieId = s?.id || '');
</script>

<style lang="scss" scoped>
:deep(.list-input-button) {
    margin-bottom: 1rem !important;
}
</style>
