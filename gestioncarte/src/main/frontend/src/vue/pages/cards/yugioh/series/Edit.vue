<template>
    <YuGiOhSerieEditForm v-model="serie" @save="save" />
</template>

<script lang="ts" setup>
import {useRoute, useRouter} from "vue-router";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {computed, ref, watch} from "vue";
import {useRaise} from "@/alert";
import {YuGiOhSerieEditForm, yugiohSerieService} from "@components/cards/yugioh/serie";
import {YuGiOhSerieDTO} from "@/types";

const route = useRoute();
const router = useRouter();
const raise = useRaise();
const serie = ref<YuGiOhSerieDTO>();

usePageTitle(computed(() => {
    const id = serie.value?.id;
    const end = id ? ` (id: ${id})` : '';

    return `Yu-Gi-Oh! - ${id ? 'Modification' : 'Ajout'} de serie${end}`;
}));

const save = async () => {
    if (serie.value) {
        serie.value = await yugiohSerieService.save(serie.value);

        await router.push(`/cards/yugioh/series/${serie.value.id}`);
        raise.success("Serie enregistrée avec succès.");
    }
};

watch(route, async route => {;
    if (route.params.serieId) {
        serie.value = await yugiohSerieService.get(route.params.serieId as string);
    } else {
        serie.value = {
            id: "",
            translations: {},
        };
    }
}, { immediate: true });

</script>
