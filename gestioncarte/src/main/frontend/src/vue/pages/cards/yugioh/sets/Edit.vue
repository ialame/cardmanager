<template>
    <Loading :ready="ready">
        <YuGiOhSetEditForm v-model="set" @save="save" />
    </Loading>
</template>

<script lang="ts" setup>
import {YuGiOhSetEditForm, yugiohSetService} from "@components/cards/yugioh/set";
import {useRoute, useRouter} from "vue-router";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {computed, ref, watch} from "vue";
import {ExtractedYuGiOhSetDTO, YuGiOhSetDTO} from "@/types";
import {useRaise} from "@/alert";
import Loading from "@components/Loading.vue";
import rest from "@/rest";
import {computedAsync} from "@vueuse/core";
import {useProvideConsolidationSources} from "@components/form/advanced";

const router = useRouter();
const route = useRoute();
const raise = useRaise();
const set = ref<YuGiOhSetDTO>();
const ready = ref(false);

usePageTitle(computed(() => {
    const id = set.value?.id;
    const end = id ? ` (id: ${id})` : '';

    return `Yu-Gi-Oh! - ${id ? 'Modification' : 'Ajout'} d'extension${end}`;
}));

const save = async () => {
    if (set.value) {
        set.value = await yugiohSetService.save(set.value);

        const extractedId = route.query['extracted-id'];

        if (extractedId) {
            await rest.delete(`/api/cards/yugioh/sets/extracted/${extractedId}`);
        }
        await router.push(`/cards/yugioh/sets/${set.value.id}`);
        raise.success("Extension enregistrée avec succès.");
    }
};

const extracted = computedAsync<ExtractedYuGiOhSetDTO>(() => {
    const id = route.query['extracted-id'];

    if (!id) {
        return undefined;
    }
    return rest.get(`/api/cards/yugioh/sets/extracted/${id}`)
})

useProvideConsolidationSources(() => extracted.value?.sources ?? []);

watch([route, extracted], async ([r, e]) => {
    const setId = r.params.setId as string;

    if (e) {
        const s = e.set;

        if (s) {
            if (setId) {
                s.id = setId;
            } else if (e.savedSets && e.savedSets.length > 0) {
                await router.push({
                    path: `/cards/yugioh/sets/${e.savedSets[0].id}`,
                    query: {'extracted-id': e.id}
                });
            }
            set.value = s;
        }
    } else if (setId) {
        set.value = await yugiohSetService.get(setId);
    } else {
        set.value = {
            id: "",
            officialSitePids: [],
            parentId: "",
            promo: false,
            serieId: "",
            translations: {},
            type: ""
        };
    }
    ready.value = true;
}, {immediate: true});

</script>
