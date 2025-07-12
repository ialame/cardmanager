<template>
    <Loading :ready="ready">
        <SideButtons>
            <ScrollToTop />
        </SideButtons>
        <div v-if="!isEmpty(sets)" class="full-screen-container mt-4">
            <FormRow>
                <Column size="sm" class="form-group">
                    <label class="form-label">Filtrer par langue</label>
                    <LocalizationSelect v-model="localizationFilter" :localizations="['us', 'jp']" :showAll="true" />
                </Column>
                <Column size="sm" class="form-group">
                    <label class="form-label">Filtrer par promo</label>
                    <select v-model="promoFilter" class="form-select form-control" aria-label="Filtrer par promo">
                        <option value="all" selected>Toutes</option>
                        <option value="false">Non Promo</option>
                        <option value="true">Promo</option>
                    </select>
                </Column>
                <Column>
                    <label class="form-label">Filtrer par Série</label>
                    <PokemonSerieSelect v-model="serieFilter" :values="serieList" hasAllOption />
                </Column>
            </FormRow>
            <hr>
            <Collapse v-for="(setList, serieId) in groupedSets" :key="serieId" :open="!!serieFilter">
                <template #label>
                    <PokemonSerieLabel :serie="serieId" />
                </template>
                <table class="table table-striped mt-4">
                    <thead>
                        <tr>
                            <th scope="col" class="min-w-100">Nom de l'extension</th>
                            <th scope="col" class="min-w-100">Langues</th>
                            <th scope="col" class="min-w-100"></th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr v-for="set in setList" :key="set.id">
                            <td>
                                <PokemonSetLabel :set="set" />
                            </td>
                            <td>
                                <Flag v-for="localization in set.translations" :key="localization" :lang="localization.localization" class="me-1" />
                            </td>
                            <td>
                                <div class="float-end">
                                    <a title="Ajouter une fille" :href="`/cards/pokemon/sets/new?from-parent=${set.id}`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                        <Icon src="/svg/parent.svg" />
                                    </a>
                                    <a title="Modifier" :href="`/cards/pokemon/sets/${set.id}`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                        <Icon name="pencil" />
                                    </a>
                                </div>
                            </td>
                        </tr>
                    </tbody>
              </table>
            </Collapse>
        </div>
    </Loading>
</template>

<script lang="ts" setup>
import {usePageTitle} from "@/vue/composables/PageComposables";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {chain, isEmpty, isNil} from "lodash";
import {PokemonSetLabel} from "@components/cards/pokemon/set";
import {Flag, LocalizationCode} from "@/localization";
import {ScrollToTop, SideButtons} from '@components/side';
import {computed, ref} from "vue";
import Loading from "@components/Loading.vue";
import Icon from "@components/Icon.vue";
import Collapse from "@components/collapse/Collapse.vue";
import FormRow from "@components/form/FormRow.vue";
import Column from "@components/grid/Column.vue";
import LocalizationSelect from "@components/form/LocalizationSelect.vue";
import {PokemonSerieDTO, PokemonSetDTO} from "@/types";
import {PokemonSerieLabel, PokemonSerieSelect, pokemonSerieService} from "@components/cards/pokemon/serie";
import {computedAsync} from "@vueuse/core";

type LocalizationFilterValue = LocalizationCode | 'all';
type PromoFilterValue = 'true' | 'false' | 'all';

usePageTitle("Pokémon - Liste des extensions");

const sets = PokemonComposables.setsService.all;
const ready = computed(() => !isEmpty(sets.value));

const promoFilter = ref<PromoFilterValue>("all");
const filterPromo = (set: PokemonSetDTO) => promoFilter.value === 'all' || set.promo === (promoFilter.value === "true");
const localizationFilter = ref<LocalizationFilterValue>('all');
const filterLocalization = (set: PokemonSetDTO) => localizationFilter.value === 'all' || !!set.translations[localizationFilter.value]?.available;
const serieFilter = ref<PokemonSerieDTO>();
const serieList = computedAsync<PokemonSerieDTO[]>(async () => localizationFilter.value === 'all' ? pokemonSerieService.all.value : await pokemonSerieService.find(serie => !!serie.translations[localizationFilter.value as LocalizationCode]), []);
const filterSerie = (set: PokemonSetDTO) => isNil(serieFilter.value) || set.serieId === serieFilter.value.id;
const groupedSets = computed(() => chain(sets.value)
    .filter(filterPromo)
    .filter(filterLocalization)
    .filter(filterSerie)
    .groupBy(set => set.serieId)
    .value());
</script>
