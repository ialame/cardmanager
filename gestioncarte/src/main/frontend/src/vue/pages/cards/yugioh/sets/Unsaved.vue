<template>
    <Loading :ready="!isEmpty(sets)">
        <div class="container mt-4">
            <FormButton @click="forceReload()" color="secondary">Recharger les extensions</FormButton>
        </div>
        <hr />
        <div class="container">
            <table class="table table-striped mt-4">
                <thead>
                    <tr>
                        <th scope="col" class="min-w-100">Nom de l'extension</th>
                        <th scope="col" class="min-w-100">Langues</th>
                        <th scope="col" class="min-w-100"></th>
                    </tr>
                </thead>
                <tbody>
                <tr v-for="(set, i) in sets" :key="i">
                    <td>
                        <YuGiOhSetLabel :set="set.set" />
                    </td>
                    <td>
                        <Flag v-for="localization in set.set.translations" :key="localization" :lang="localization.localization" class="me-1" />
                    </td>
                    <td>
                        <div class="float-end">
                            <RouterLink title="Ajouter" :to="getRoute(set)" target="_blank" rel="noopener" class="btn btn-link me-2 p-0 no-focus">
                                <Icon name="add-outline" />
                            </RouterLink>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </div>
    </Loading>
</template>

<script lang="ts" setup>
import {ExtractedYuGiOhSetDTO} from "@/types";
import {YuGiOhSetLabel} from "@components/cards/yugioh/set";
import Flag from "@/localization/Flag.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import Icon from "@components/Icon.vue";
import {RouteLocationRaw, RouterLink} from "vue-router";
import Loading from "@components/Loading.vue";
import {RestComposables} from "@/vue/composables/RestComposables";
import {computed, onMounted} from "vue";
import rest from "@/rest";
import {timeout} from "@/retriever";
import {cloneDeep, isEmpty} from "lodash";
import FormButton from "@components/form/FormButton.vue";

usePageTitle("Yu-Gi-Oh! - Liste des nouvelles extensions");

const {state: rawSets, refresh} = RestComposables.useRestComposable<ExtractedYuGiOhSetDTO[], [], true>("/api/cards/yugioh/sets/extracted", [], {fullState: true});

const sets = computed(() => {
    const copy = cloneDeep(rawSets.value);

    for (const set of copy) {
        set.set.id = set.savedSets?.[0]?.id;
    }
    return copy;
});

const getRoute = (set: ExtractedYuGiOhSetDTO): RouteLocationRaw => ({
    path: set.set?.id ? `/cards/yugioh/sets/${set.set.id}` : '/cards/yugioh/sets/new',
    query: { 'extracted-id': set.id }
});

const reload = async () => {
    await rest.post("/api/cards/yugioh/sets/extract");
    while ((await refresh()).length === 0) {
        await timeout(500);
    }
}
const forceReload = async () => {
    await rest.delete("/api/cards/yugioh/sets/extracted");
    await refresh();
    await reload();
}

onMounted(async () => {
    if ((await refresh()).length > 0) {
        return;
    }
    await reload();
});
</script>
