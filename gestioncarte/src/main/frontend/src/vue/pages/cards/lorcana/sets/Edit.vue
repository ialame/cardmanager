<template>
    <Loading :ready="ready">
        <LorcanaSetEditForm v-model="set" @save="save" />
    </Loading>
</template>

<script lang="ts" setup>
import {useRoute, useRouter} from "vue-router";
import {computed, ref, watch} from "vue";
import {LorcanaSetDTO} from "@/types";
import {useRaise} from "@/alert";
import Loading from "@components/Loading.vue";
import LorcanaSetEditForm from "@components/cards/lorcana/set/edit/LorcanaSetEditForm.vue";
import {lorcanaSetService} from "@components/cards/lorcana/set";
import rest from "@/rest";
import {ConfirmComposables} from "@/vue/composables/ConfirmComposables";
import {useLorcanaTitle} from "@components/cards/lorcana/logic";
import confirmOrCancel = ConfirmComposables.confirmOrCancel;

const router = useRouter();
const raise = useRaise();
const set = ref<LorcanaSetDTO>();
const ready = ref(false);

useLorcanaTitle(computed(() => {
    const id = set.value?.id;
    const end = id ? ` (id: ${id})` : '';

    return `${id ? 'Modification' : 'Ajout'} d'extension${end}`;
}));

const save = async () => {
    if (set.value) {
        set.value = await lorcanaSetService.save(set.value);
        await router.push(`/cards/lorcana/sets/${set.value.id}`);
        raise.success("Extension enregistrée avec succès.");
    }
};

watch(useRoute(), async route => {
    const code = route.query['code'] as string;

    if (code) {
        const s = await rest.get(`/api/cards/lorcana/sets/unsaved/${code}/`) as LorcanaSetDTO;

        if (s.serieId) {
            set.value = s;
        } else if (await confirmOrCancel("L'extension n'a pas de série associée. Ajouter une série ?")) {
            await router.push({
                path: '/cards/lorcana/series/new',
                query: {
                    'code': code
                }
            });
        } else {
            await router.push('/cards/lorcana/sets/unsaved');
        }
    } else if (route.params.setId) {
        set.value = await lorcanaSetService.get(route.params.setId as string);
    } else {
        set.value = {
            code: "",
            id: "",
            officialSiteIds: [],
            promo: false,
            serieId: "",
            translations: {}
        };
    }
    ready.value = true;
}, { immediate: true });

</script>
