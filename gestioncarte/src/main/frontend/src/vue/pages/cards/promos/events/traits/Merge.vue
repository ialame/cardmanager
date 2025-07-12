<template>
    <div class="container">
        <PromoCardEventTraitEditForm v-model="trait" :mergeSources="saveTraits" @save="save()" />
    </div>
</template>

<script lang="ts" setup>
import {useRouter} from "vue-router";
import {ref, watchEffect} from "vue";
import {PromoCardEventTraitDTO} from "@/types";
import {promoCardEventTraitService} from "@components/cards/promo";
import {useCommaSeparatedParam, usePageTitle} from "@/vue/composables/PageComposables";
import {useRaise} from "@/alert";
import PromoCardEventTraitEditForm from "@components/cards/promo/event/trait/PromoCardEventTraitEditForm.vue";
import {computedAsync} from "@vueuse/core";
import {cloneDeep} from "lodash";
import {TradingCardGame, useTcg} from "@/tcg";

const tcg = useTcg();
const router = useRouter();
const raise = useRaise();

const traitIds = useCommaSeparatedParam('traitIds');
const trait = ref<PromoCardEventTraitDTO>({
    id: "",
    tcg: tcg.value as TradingCardGame,
    name: "",
    translations: {},
    type: ""
});

usePageTitle(`Fusion de carcteristique d'evenement promotionel`);

const saveTraits = computedAsync(() => Promise.all(traitIds.value.map(id => promoCardEventTraitService.get(id))), []);

const save = async () => {
    if (!trait.value) {
        return;
    }

    trait.value = await promoCardEventTraitService.merge(trait.value, traitIds.value);
    await router.push(`/cards/${tcg.value}/promos/events/traits/${trait.value.id}`);
    raise.success(`La carcteristique d'evenement promotionel a bien été sauvegardée.`);
};

watchEffect(async () => {
    if (saveTraits.value && saveTraits.value.length > 0) {
        trait.value = cloneDeep(saveTraits.value[0]);
    }
}, {flush: 'post'});
</script>
