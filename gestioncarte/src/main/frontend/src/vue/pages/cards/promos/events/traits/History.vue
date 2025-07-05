<template>
	<Loading :ready="history !== undefined">
        <div class="full-screen-container mt-4">
            <HistoryView v-if="history" :modelValue="history">
                <template v-slot="{modelValue: v, parents: parents}">
                    <div class="container border rounded light-bg">
                        <PromoCardEventTraitEditForm :modelValue="v" :mergeSources="parents" disabled />
                    </div>
                </template>
            </HistoryView>
        </div>
	</Loading>
</template>

<script lang="ts" setup>
import {HistoryTreeDTO, PromoCardEventTraitDTO} from '@/types';
import {ref, watch} from 'vue';
import HistoryView from "@components/HistoryView.vue";
import {computedAsync} from "@vueuse/core";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRoute, useRouter} from "vue-router";
import Loading from "@components/Loading.vue";
import PromoCardEventTraitEditForm from "@components/cards/promo/event/trait/PromoCardEventTraitEditForm.vue";
import {promoCardEventTraitService} from "@components/cards/promo";
import {useTcg} from "@/tcg";

usePageTitle("Historique carcteristique d'evenement promotionel");

const tcg = useTcg();
const route = useRoute();
const router = useRouter();
const traitId = ref<string>(route.params.traitId as string || '');

const history = computedAsync<HistoryTreeDTO<PromoCardEventTraitDTO>>(async () => {
    if (traitId.value) {
        return promoCardEventTraitService.history(traitId.value);
    }
    return undefined;
});


watch(history, v => {
    if (v) {
        const newId = v.revision.data?.id;

        if (newId) {
            traitId.value = newId;
            router.replace(`/cards/${tcg.value}/promos/events/traits/${newId}/history`);
        }
    }
});
</script>
