<template>
    <Loading :ready="ready">
        <SideButtons>
            <ScrollToTop />
        </SideButtons>
        <div v-if="!isEmpty(cards)" class="full-screen-container mt-4">
            <div class="d-flex flex-row">
                <LorcanaSetLabel :set="set" />
                <FormButton color="secondary" class="ms-auto" @click="rebuildIdPrim">Regenerer les Id Prom</FormButton>
            </div>
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">Number</th>
                        <th scope="col">Name</th>
                        <th scope="col">Langues</th>
                        <th scope="col" class="min-w-100" />
                    </tr>
                </thead>
                <tbody>
                    <tr v-for="card in cards" :key="card.id">
                        <td>{{ card.translations.us.fullNumber }}</td>
                        <td>{{ getLorcanaCardName(card) }}</td>
                        <td>
                            <div class="d-flex flex-row">
                                <Flag v-for="l in Object.keys(card.translations)" :key="l" :lang="l" class="me-1" />
                            </div>
                        </td>
                        <td>
                            <div class="float-end">
                                <a title="Modifier" :href="`/cards/lorcana/new?from=${card.id}`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                    <Icon name="copy-outline" />
                                </a>
                                <a title="Modifier" :href="`/cards/lorcana/${card.id}`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                    <Icon name="pencil" />
                                </a>
                            </div>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
        <div v-else class="d-flex justify-content-center mt-2">
            <h4>Aucune carte trouvé</h4>
        </div>
    </Loading>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import {LorcanaCardDTO} from '@/types';
import {isEmpty, isNil} from 'lodash';
import {computed, Ref, ref} from 'vue';
import {ScrollToTop, SideButtons} from '@components/side';
import {RestComposables} from "@/vue/composables/RestComposables";
import {useRoute} from "vue-router";
import Loading from "@components/Loading.vue";
import {computedAsync} from "@vueuse/core";
import FormButton from '@components/form/FormButton.vue';
import {useRaise} from "@/alert";
import {getLorcanaCardName, useLorcanaTitle} from "@components/cards/lorcana/logic";
import {LorcanaSetLabel, lorcanaSetService} from "@components/cards/lorcana/set";
import {Flag} from "@/localization";
import Icon from "@components/Icon.vue";

useLorcanaTitle("Liste de cartes");

const route = useRoute();
const raise = useRaise();

const setId = ref<string>(route.params.setId as string || '');
const cards = RestComposables.useRestComposable(() => rest.get(`/api/cards/lorcana/sets/${setId.value}/cards`), undefined) as Ref<LorcanaCardDTO[] | undefined>;
const set = computedAsync(() => lorcanaSetService.get(setId.value));

const ready = computed(() => !isNil(cards.value) && !isNil(set.value) && !isEmpty(set.value));

const rebuildIdPrim = async () => {
    await rest.post(`/api/cards/lorcana/sets/${setId.value}/rebuild-ids-prim`);
    raise.success("Les Id Prom ont été regénérés");
}
</script>
