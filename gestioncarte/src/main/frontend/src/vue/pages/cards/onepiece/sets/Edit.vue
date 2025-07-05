<template>
    <Loading :ready="ready">
        <OnePieceSetEditForm v-model="set" @save="save" />
    </Loading>
</template>

<script lang="ts" setup>
import {useRoute, useRouter} from "vue-router";
import {computed, ref, watch} from "vue";
import {OnePieceSetDTO} from "@/types";
import {useRaise} from "@/alert";
import Loading from "@components/Loading.vue";
import OnePieceSetEditForm from "@components/cards/onepiece/set/edit/OnePieceSetEditForm.vue";
import {onePieceSetService} from "@components/cards/onepiece/set";
import rest from "@/rest";
import {ConfirmComposables} from "@/vue/composables/ConfirmComposables";
import {useOnePieceTitle} from "@components/cards/onepiece/logic";
import confirmOrCancel = ConfirmComposables.confirmOrCancel;

const router = useRouter();
const raise = useRaise();
const set = ref<OnePieceSetDTO>();
const ready = ref(false);

useOnePieceTitle(computed(() => {
    const id = set.value?.id;
    const end = id ? ` (id: ${id})` : '';

    return `${id ? 'Modification' : 'Ajout'} d'extension${end}`;
}));

const save = async () => {
    if (set.value) {
        set.value = await onePieceSetService.save(set.value);
        await router.push(`/cards/onepiece/sets/${set.value.id}`);
        raise.success("Extension enregistrée avec succès.");
    }
};

watch(useRoute(), async route => {
    const code = route.query['code'] as string;

    if (code) {
        const s = await rest.get(`/api/cards/onepiece/sets/unsaved/${code}`) as OnePieceSetDTO;

        if (!s) {
            raise.error("Aucune extension trouvée avec ce code.");
            router.back();
            return;
        }
        if (s.serieId) {
            set.value = s;
        } else if (await confirmOrCancel("L'extension n'a pas de série associée. Ajouter une série ?")) {
            await router.push({
                path: '/cards/onepiece/series/new',
                query: {
                    'code': code
                }
            });
        } else {
            router.back();
        }
    } else if (route.params.setId) {
        set.value = await onePieceSetService.get(route.params.setId as string);
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
