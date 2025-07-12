<template>
    <OnePieceSerieEditForm v-model="serie" @save="save" />
</template>

<script lang="ts" setup>
import {useRoute, useRouter} from "vue-router";
import {computed, ref, watchEffect} from "vue";
import rest from "@/rest";
import {useRaise} from "@/alert";
import {OnePieceSerieDTO} from "@/types";
import OnePieceSerieEditForm from "@components/cards/onepiece/serie/edit/OnePieceSerieEditForm.vue";
import {onePieceSerieService} from "@components/cards/onepiece/serie";
import {useOnePieceTitle} from "@components/cards/onepiece/logic";

const route = useRoute();
const router = useRouter();
const raise = useRaise();
const serie = ref<OnePieceSerieDTO>();

const code = computed(() => route.query['code']);

useOnePieceTitle(computed(() => {
    const id = serie.value?.id;
    const end = id ? ` (id: ${id})` : '';

    return `${id ? 'Modification' : 'Ajout'} de serie${end}`;
}));

const save = async () => {
    if (serie.value) {
        serie.value = await onePieceSerieService.save(serie.value);


        if (code.value) {
            await router.push({
                path: '/cards/onepiece/sets/new',
                query: {
                    'code': code.value
                }
            });
            raise.info("Vous avez été redirigé vers la création de l'extension.");
        } else {
            await router.push(`/cards/onepiece/series/${serie.value.id}`);
        }
        raise.success("Serie enregistrée avec succès.");
    }
};

watchEffect(async () => {
    if (code.value) {
        serie.value = await rest.get(`/api/cards/onepiece/series/unsaved/${code.value}`) as OnePieceSerieDTO;
    } else if (route.params.serieId) {
        serie.value = await onePieceSerieService.get(route.params.serieId as string);
    } else {
        serie.value = {
            id: "",
            translations: {},
        };
    }
});

</script>
