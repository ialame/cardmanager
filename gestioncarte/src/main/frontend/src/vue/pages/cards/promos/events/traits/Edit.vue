<template>
    <div class="container">
        <PromoCardEventTraitEditForm v-model="trait" @save="save()" />
    </div>
</template>

<script lang="ts" setup>
import {useRoute, useRouter} from "vue-router";
import {computed, ref, watchEffect} from "vue";
import {PromoCardEventTraitDTO} from "@/types";
import {promoCardEventTraitService} from "@components/cards/promo";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRaise} from "@/alert";
import PromoCardEventTraitEditForm from "@components/cards/promo/event/trait/PromoCardEventTraitEditForm.vue";
import {TradingCardGame} from "@/tcg";

const route = useRoute();
const router = useRouter();
const raise = useRaise();
const traitId = computed(() => route.params.traitId as string || '');
const tcg = computed(() => route.params.tcg as string || '');
const trait = ref<PromoCardEventTraitDTO>({
    id: "",
    tcg: tcg.value as TradingCardGame,
    name: "",
    translations: {},
    type: ""
});

usePageTitle(computed(() => {
    const edit = trait.value.id;
    const end = edit ? ` (id: ${trait.value.id})` : '';

    return `${edit? 'Modification' : 'Ajout'} de carcteristique d'evenement promotionel${end}`;
}));

const save = async () => {
    if (!trait.value) {
        return;
    }

    trait.value = await promoCardEventTraitService.save(trait.value);
    await router.push(`/cards/${tcg.value}/promos/events/traits/${trait.value.id}`);
    raise.success(`La carcteristique d'evenement promotionel a bien été sauvegardée.`);
};

watchEffect(async () => {
    if (traitId.value) {
        trait.value = await promoCardEventTraitService.get(traitId.value);
    }
}, {flush: 'post'});
</script>
