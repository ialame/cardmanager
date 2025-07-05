<template>
    <SideButtons>
        <ScrollToTop />
    </SideButtons>
  <div class="container">
    <FormRow>
      <Column size="sm" class="form-group">
        <label class="form-label">Filtrer par langue</label>
        <LocalizationSelect v-model="localizationFilter" :localizations="localizationCodes" :showAll="true" />
      </Column>
    </FormRow>
      <div v-if="!isEmpty(groupedTraits)">
        <collapse v-for="(t,index) in groupedTraits" :key="index" :open="false">
          <template #label>
            <span>{{index}}</span>
          </template>

          <table class="table table-striped mt-4">
            <thead>
            <tr>
              <th scope="col">Caracteristique</th>
              <th scope="col">Langues</th>
              <th scope="col">Type</th>
              <th scope="col" class="min-w-100"></th>
            </tr>
            </thead>
            <tbody>
            <tr v-for="trait in t" :key="trait.id">
              <td><PromoCardEventTraitLabel :trait="trait" /></td>
              <td><Flag v-for="l in Object.keys(trait.translations)" :key="l" class="me-1" :lang="l as LocalizationCode" /></td>
              <td>{{ trait.type }}</td>
              <td>
                <div class="float-end" v-if="trait.id">
                  <IconButton v-if="traitGroups[trait.name.toLowerCase()]" name="git-merge" title="Fusioner les carateristiques" @Click="mergeTraits(traitGroups[trait.name.toLowerCase()])" class="me-2 p-0 no-focus" />
                  <IconButton name="time-outline" title="Historique de la carateristiques" @Click="showHistory(trait.id)" class="me-2 p-0 no-focus" />
                  <IconButton name="pencil" title="Modifier la carateristique" @Click="editTrait(trait.id)" class="me-2 p-0 no-focus" />
                </div>
              </td>
            </tr>
            </tbody>
          </table>
        </collapse>
      </div>
    </div>
</template>

<script lang="ts" setup>
import LocalizationSelect from "@components/form/LocalizationSelect.vue";
import {chain, isEmpty} from 'lodash';
import {ScrollToTop, SideButtons} from '@components/side';
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRouter} from "vue-router";
import {promoCardEventTraitService} from "@components/cards/promo";
import {Flag, LocalizationCode, localizationCodes} from "@/localization";
import PromoCardEventTraitLabel from "@components/cards/promo/event/trait/PromoCardEventTraitLabel.vue";
import {computed, ref} from "vue";
import IconButton from "@components/form/IconButton.vue";
import Collapse from "@components/collapse/Collapse.vue";
import {PromoCardEventTraitDTO} from "@/types";
import {getTcg, useTcg} from "@/tcg";


type LocalizationFilterValue = LocalizationCode | 'all';
usePageTitle("Liste des carcteristiques d'evenement promotionel");

const tcg = useTcg();
const router = useRouter();
const traits = promoCardEventTraitService.all;
const localizationFilter = ref<LocalizationFilterValue>('all');
const traitGroups = computed(() => chain(traits.value)
    .filter(t => !tcg.value || t.tcg === tcg.value)
    .groupBy(t => t.name.toLowerCase())
    .pickBy(t => t.length > 1)
    .mapValues(t => t.map(tt => tt.id))
    .value());


const filterLocalization = (trait : PromoCardEventTraitDTO ) => localizationFilter.value === 'all' || !!trait.translations[localizationFilter.value];
const groupedTraits = computed(() => chain(traits.value)
    .filter(t =>!tcg.value || getTcg(t.tcg) === tcg.value)
    .filter(filterLocalization)
    .groupBy(trait => trait.type)
    .value());

const editTrait = (id: string) => {
    if (id) {
        router.push(`/cards/${tcg.value}/promos/events/traits/${id}`);
    }
};
const showHistory = (id: string) => {
    if (id) {
        router.push(`/cards/${tcg.value}/promos/events/traits/${id}/history`);
    }
};
const mergeTraits = (ids?: string[]) => {
    if (ids && ids.length > 0) {
        router.push(`/cards/${tcg.value}/promos/events/traits/merge/${ids.join(',')}`);
    }
};

</script>
