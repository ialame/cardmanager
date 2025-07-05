<template>
    <SideButtons>
        <ScrollToTop />
    </SideButtons>
    <div class="container mt-3">
        <FormRow>
            <FormInput label="Filtre" v-model="filter" />
        </FormRow>
        <template v-if="!isEmpty(computedCache)">
            <hr>
            <template v-if="computedCache.length > 100">
                <div class="d-flex justify-content-center mt-2">
                    <h4>{{computedCache.length}} pages pour ce filtre</h4>
                </div>
            </template>
            <table v-else class="table table-striped mt-4">
                <thead>
                <tr>
                    <th scope="col">URL</th>
                    <th scope="col" class="min-w-100"></th>
                </tr>
                </thead>
                <tbody>
                <tr v-for="cache in computedCache" :key="cache">
                    <td>
                        <a v-if="cache.startsWith('http')" :href="cache" target="_blank" rel="noopener noreferrer">{{ cache }}</a>
                        <span v-else>{{ cache }}</span>
                    </td>
                    <td>
                        <div class="float-end">
                            <FormButton color="link" title="Modifier la carte" @Click="deletePage(cache)" class="me-2 p-0 no-focus">
                                <Icon class="text-danger" name="trash" />
                            </FormButton>
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
        </template>
    </div>
</template>

<script lang="ts" setup>
import {RestComposables} from "@/vue/composables/RestComposables";
import {isEmpty} from 'lodash';
import FormButton from "@components/form/FormButton.vue";
import {ScrollToTop, SideButtons} from '@components/side';
import rest from "@/rest";
import {computed, ref} from "vue";
import FormRow from "@components/form/FormRow.vue";
import FormInput from "@components/form/FormInput.vue";
import Icon from "@components/Icon.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRaise} from "@/alert";

usePageTitle("Pages en cache");

const raise = useRaise();

const filter = ref("");
const {state: caches, refresh} = RestComposables.useRestComposable<string[], any[], true>('/api/caches/pages', [], {fullState: true});
const computedCache = computed(() => {
    const f = decodeURIComponent(filter.value).toLowerCase();

    return caches.value
        .map(cache => decodeURIComponent(cache).replace(/\+/, "_"))
        .filter(cache => cache.toLowerCase().includes(f));
});

const deletePage = async (url: string) => {
    if (url) {
        await rest.delete(`/api/caches/pages/`, {
            data: [url]
        });
        refresh();
        raise.success("Page supprimée");
    }
};

</script>
