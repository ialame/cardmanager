<template>
    <div class="container mt-4">
        <div class="d-flex flex-row">
            <YuGiOhSetLabel :set="set" />
            <FormButton color="secondary" class="ms-auto" @click="rebuildIdPrim">Regenerer les Id Prom</FormButton>
        </div>
        <table class="table table-striped">
            <thead>
            <tr>
                <th scope="col" class="min-w-100">Numero</th>
                <th scope="col" class="min-w-100">Nom de la carte</th>
                <th scope="col" class="min-w-100">Rareté</th>
                <th scope="col" class="min-w-100">Langues</th>
                <th scope="col" class="min-w-100" />
            </tr>
            </thead>
            <tbody>
            <tr v-for="card in cards" :key="card.id">
                <td>{{getYuGiOhCardNumber(card)}} <IdTooltip :id="card.id" /></td>
                <td>{{getYuGiOhCardName(card)}}</td>
                <td>{{getYuGiOhCardRarity(card)}}</td>
                <td>
                    <Flag v-for="t in card.translations" :key="t.localization" :lang="t.localization" class="me-1" />
                </td>
                <td>
                    <div class="float-end">
                        <a title="Modifier" :href="`/cards/yugioh/${card.id}`" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                            <Icon name="pencil" />
                        </a>
                    </div>
                </td>
            </tr>
            </tbody>
        </table>
    </div>
</template>

<script lang="ts" setup>
import {Flag} from "@/localization";
import {getYuGiOhCardName, getYuGiOhCardNumber, getYuGiOhCardRarity} from "@components/cards/yugioh";
import {useRoute} from "vue-router";
import {computed, Ref} from "vue";
import {RestComposables} from "@/vue/composables/RestComposables";
import rest from "@/rest";
import {YuGiOhCardDTO} from "@/types";
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import Icon from "@components/Icon.vue";
import FormButton from "@components/form/FormButton.vue";
import {YuGiOhSetLabel, yugiohSetService} from "@components/cards/yugioh/set";
import {computedAsync} from "@vueuse/core";
import {useRaise} from "@/alert";

const route = useRoute();
const raise = useRaise();

const setId = computed(() => route.params.setId as string);
const set = computedAsync(() => yugiohSetService.get(setId.value));
const cards = RestComposables.useRestComposable(() => rest.get(`/api/cards/yugioh/sets/${setId.value}/cards`), undefined) as Ref<YuGiOhCardDTO[] | undefined>;
const rebuildIdPrim = async () => {
    await rest.post(`/api/cards/yugioh/sets/${setId.value}/rebuild-ids-prim`);
    raise.success("Les Id Prom ont été regénérés");
}
</script>
