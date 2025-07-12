<template>
    <LorcanaSerieEditForm v-model="serie" @save="save" />
</template>

<script lang="ts" setup>
import {useRoute, useRouter} from "vue-router";
import {computed, ref, watchEffect} from "vue";
import {useRaise} from "@/alert";
import {LorcanaSerieDTO} from "@/types";
import LorcanaSerieEditForm from "@components/cards/lorcana/serie/edit/LorcanaSerieEditForm.vue";
import {lorcanaSerieService} from "@components/cards/lorcana/serie";
import {useLorcanaTitle} from "@components/cards/lorcana/logic";

const route = useRoute();
const router = useRouter();
const raise = useRaise();
const serie = ref<LorcanaSerieDTO>();


useLorcanaTitle(computed(() => {
    const id = serie.value?.id;
    const end = id ? ` (id: ${id})` : '';

    return `${id ? 'Modification' : 'Ajout'} de serie${end}`;
}));

const save = async () => {
    if (serie.value) {
        serie.value = await lorcanaSerieService.save(serie.value);

        await router.push(`/cards/lorcana/series/${serie.value.id}`);
        raise.success("Serie enregistrée avec succès.");
    }
};

watchEffect(async () => {
    if (route.params.serieId) {
        serie.value = await lorcanaSerieService.get(route.params.serieId as string);
    } else {
        serie.value = {
            id: "",
            translations: {},
        };
    }
});

</script>
