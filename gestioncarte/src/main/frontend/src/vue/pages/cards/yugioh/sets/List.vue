<template>
  <div class="container mt-4">
    <FormRow>
      <Column size="sm" class="form-group">
        <label class="form-label">Filtrer par langue</label>
        <LocalizationSelect v-model="localizationFilter" :localizations="localizationCodes" :showAll="true" />
      </Column>
      <Column>
        <label class="form-label">Filtrer par Série</label>
        <YuGiOhSerieSelect v-model="serieFilter" :values="serieList" hasAllOption />
      </Column>
    </FormRow>
    <hr>
    <Collapse v-for="(i, serieId) in groupedSets" :key="serieId" :open="!!serieFilter">
      <template #label>
        <YuGiOhSerieLabel :serie="serieId" />
      </template>
      <table class="table table-striped">
        <thead>
        <tr>
          <th scope="col" class="min-w-100">Nom</th>
        </tr>
        </thead>
        <tbody>
        <tr v-for="set in i" :key="set.id">
          <td><YuGiOhSetLabel :set="set" /></td>
          <td>
            <div class="float-end">
              <a title="Modifier" :href="`/cards/yugioh/sets/${set.id}`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                <Icon name="pencil" />
              </a>
            </div>
          </td>
        </tr>
        </tbody>
      </table>
    </collapse>
  </div>
</template>
<script lang="ts" setup>
import {useRoute} from "vue-router";
import {Ref} from "vue";
import {RestComposables} from "@/vue/composables/RestComposables";
import rest from "@/rest";
import { YuGiOhSerieDTO, YuGiOhSetDTO} from "@/types";
import YuGiOhSetLabel from "@components/cards/yugioh/set/YuGiOhSetLabel.vue";
import Icon from "@components/Icon.vue";
import {chain, isNil} from "lodash";
import {computed, ref} from "vue";
import {computedAsync} from "@vueuse/core";
import {LocalizationCode, localizationCodes} from "@/localization";
import LocalizationSelect from "@components/form/LocalizationSelect.vue";
import Collapse from "@components/collapse/Collapse.vue";
import { YuGiOhSerieSelect, yugiohSerieService} from "@components/cards/yugioh/serie";
import YuGiOhSerieLabel from "@components/cards/yugioh/serie/YuGiOhSerieLabel.vue";
import Column from "@components/grid/Column.vue";
import FormRow from "@components/form/FormRow.vue";

type LocalizationFilterValue = LocalizationCode | 'all';

const route = useRoute();
const serieList = computedAsync<YuGiOhSerieDTO[]>(async () => localizationFilter.value === 'all' ? yugiohSerieService.all.value : await yugiohSerieService.find(serie => !!serie.translations[localizationFilter.value as LocalizationCode]), []);
const sets = RestComposables.useRestComposable(() => rest.get(`/api/cards/yugioh/sets`), undefined) as Ref<YuGiOhSetDTO[] | undefined>;

const localizationFilter = ref<LocalizationFilterValue>('all');
const filterLocalization = (set: YuGiOhSerieDTO) => localizationFilter.value === 'all' || !!set.translations[localizationFilter.value];

const serieFilter = ref<YuGiOhSerieDTO>();
const filterSerie = (set: YuGiOhSetDTO) => isNil(serieFilter.value) || set.serieId === serieFilter.value.id;
const groupedSets = computed(() => chain(sets.value)
    .filter(filterSerie)
    .filter(filterLocalization)
    .groupBy(set => set.serieId)
    .value());



</script>