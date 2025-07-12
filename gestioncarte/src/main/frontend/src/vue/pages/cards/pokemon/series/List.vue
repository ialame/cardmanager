<template>
    <Loading :ready="ready">
        <SideButtons>
            <ScrollToTop />
        </SideButtons>
        <div class="full-screen-container mt-4">
            <FormRow>
                <Column size="4" class="form-group">
                    <label class="form-label">Filtrer par langue</label>
                    <LocalizationSelect v-model="localizationFilter" :localizations="localizationCodes" :showAll="true" />
                </Column>
            </FormRow>
            <div v-if="!isEmpty(series)">


                <table class="table table-striped mt-4">
                    <thead>
                        <th>Nom</th>
                    </thead>
                    <tbody>
                        <tr v-for="serie in filteredSeries" :key="serie.id">
                            <td>
                                <PokemonSerieLabel :serie="serie.id"/>
                            </td>
                            <td>
                                <a title="Modifier" :href="`/cards/pokemon/series/${serie.id}`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                    <Icon name="pencil" />
                                </a>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div v-else class="d-flex justify-content-center mt-2">
                <h4>Aucune série trouvé</h4>
            </div>
        </div>


    </Loading>
</template>

<script lang="ts" setup>
import LocalizationSelect from "@components/form/LocalizationSelect.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {RestComposables} from "@/vue/composables/RestComposables";
import PokemonSerieLabel from "@components/cards/pokemon/serie/PokemonSerieLabel.vue";
import Icon from "@components/Icon.vue";
import {chain, isEmpty} from 'lodash';
import rest from "@/rest";
import {computed, Ref, ref} from "vue";
import {PokemonSerieDTO} from "@/types";
import {LocalizationCode} from "@/localization";
import Column from "@components/grid/Column.vue";



type LocalizationFilterValue = LocalizationCode | 'all';
const series = RestComposables.useRestComposable(() => rest.get(`/api/cards/pokemon/series`), undefined) as Ref<PokemonSerieDTO[] | undefined>;
const localizationFilter = ref<LocalizationFilterValue>('all');
const filterLocalization = (series : PokemonSerieDTO ) => localizationFilter.value === 'all' || !!series.translations[localizationFilter.value];
const filteredSeries = computed(() => chain(series.value)
    .filter(filterLocalization)
    .value());
usePageTitle("Pokémon - Liste des séries");
</script>
