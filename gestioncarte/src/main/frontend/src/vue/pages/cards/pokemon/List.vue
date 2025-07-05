<template>
    <Loading :ready="ready">
        <SideButtons>
            <ScrollToTop />
        </SideButtons>
        <div v-if="!isEmpty(cards)" class="full-screen-container mt-4">
            <div class="d-flex flex-row">
                <PokemonSetLabel :set="set" />
                <FormButton v-if="expansionsBulbapedia.length > 0" color="bulbapedia" :href="expansionsBulbapedia[0].url" :newTab="true" class="ms-2 round-btn-sm">
                  <BulbapediaIcon />
                </FormButton>
                <FormButton color="secondary" class="ms-auto" @click="rebuildIdPrim">Regenerer les Id Prom</FormButton>
            </div>
            <ListTable :cards="cards" :mainLocalization="mainLocalization" @openCard="showInfo">
                <template #afterHeader>
                    <th scope="col" class="min-w-100" />
                </template>
                <template #afterEntry="{card}">
                    <td>
                        <div class="float-end">
                            <a title="Ajouter une fille" :href="`/cards/pokemon/new/?from-parent=${card.id}`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                <Icon src="/svg/parent.svg" />
                            </a>
                            <a title="Historique" :href="`/cards/pokemon/${card.id}/history`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                <Icon name="time-outline" />
                            </a>
                            <a title="Modifier" :href="`/cards/pokemon/${card.id}/edit`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                <Icon name="pencil" />
                            </a>
                        </div>
                    </td>
                </template>
            </ListTable>
        </div>
        <div v-else class="d-flex justify-content-center mt-2">
            <h4>Aucune carte trouvé</h4>
        </div>
        <Modal ref="infoCardModal" label="Info de la carte" size="xxl">
           <PokemonCardEditForm v-if="infoCard" v-model="infoCard" :disabled="true" />
        </Modal>
    </Loading>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import {ExpansionBulbapediaDTO, PokemonCardDTO} from '@/types';
import Modal from '@components/modal/Modal.vue';
import {isEmpty, isNil} from 'lodash';
import {computed, Ref, ref} from 'vue';
import {ScrollToTop, SideButtons} from '@components/side';
import {RestComposables} from "@/vue/composables/RestComposables";
import ListTable from "@components/cards/pokemon/ListTable.vue";
import Icon from "@components/Icon.vue";
import {useRoute} from "vue-router";
import Loading from "@components/Loading.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {LocalizationCode} from "@/localization";
import PokemonSetLabel from "@components/cards/pokemon/set/PokemonSetLabel.vue";
import {computedAsync} from "@vueuse/core";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import BulbapediaIcon from "@components/cards/pokemon/BulbapediaIcon.vue";
import FormButton from '@components/form/FormButton.vue';
import {PokemonCardEditForm} from "@components/cards/pokemon/edit";
import {useRaise} from "@/alert";
import pokemonSetService = PokemonComposables.pokemonSetService;

usePageTitle("Pokémon - Liste de cartes");

const route = useRoute();
const raise = useRaise();

const setId = ref<string>(route.params.setId as string || '');
const cards = RestComposables.useRestComposable(() => rest.get(`/api/cards/pokemon/sets/${setId.value}/cards`), undefined) as Ref<PokemonCardDTO[] | undefined>;
const set = computedAsync(() => pokemonSetService.get(setId.value));
const expansionsBulbapedia = RestComposables.useRestComposable(() => rest.get(`/api/cards/pokemon/sets/${setId.value}/bulbapedia`), []) as Ref<ExpansionBulbapediaDTO[]>;
const mainLocalization = computed<LocalizationCode>(() => set.value?.translations?.jp?.available ? 'jp' : 'us');

const infoCard = ref<PokemonCardDTO>();
const infoCardModal = ref<typeof Modal>();
const showInfo = (c: PokemonCardDTO) => {
    infoCard.value = c;
    infoCardModal.value?.show();
}

const ready = computed(() => !isNil(cards.value) && !isNil(set.value) && !isEmpty(set.value));
const rebuildIdPrim = async () => {
    await rest.post(`/api/cards/pokemon/sets/${setId.value}/rebuild-ids-prim`);
    raise.success("Les Id Prom ont été regénérés");
}
</script>
