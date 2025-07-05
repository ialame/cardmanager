<template>
    <div class="container">
        <PromoCardVersionEditForm v-model="version" @save="save()" />
    </div>
</template>

<script lang="ts" setup>
import {useRoute, useRouter} from "vue-router";
import {computed, ref, watchEffect} from "vue";
import {PromoCardVersionDTO} from "@/types";
import {promoCardVersionService} from "@components/cards/promo";
import PromoCardVersionEditForm from "@components/cards/promo/version/PromoCardVersionEditForm.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRaise} from "@/alert";
import {TradingCardGame, useTcg} from "@/tcg";

const tcg = useTcg();
const route = useRoute();
const router = useRouter();
const raise = useRaise();
const versionId = computed(() => route.params.versionId as string || '');
const version = ref<PromoCardVersionDTO>({
    hidden: false,
    id: "",
    name: "",
    translations: {},
    tcg: tcg.value as TradingCardGame,
});

usePageTitle(computed(() => {
    const edit = version.value.id;
    const end = edit ? ` (id: ${version.value.id})` : '';

    return `${edit? 'Modification' : 'Ajout'} de version de promotion${end}`;
}));

const save = async () => {
    if (!version.value) {
        return;
    }

    version.value = await promoCardVersionService.save(version.value);
    await router.push(`/cards/${tcg.value}/promos/versions/${version.value.id}`);
    raise.success(`La version de promotion a bien été sauvegardée.`);
};

watchEffect(async () => {
    if (versionId.value) {
        version.value = await promoCardVersionService.get(versionId.value);
    }
}, {flush: 'post'});
</script>
