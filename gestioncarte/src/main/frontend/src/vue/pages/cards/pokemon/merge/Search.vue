<template>
    <div class="container mt-4">
        <FormRow>
            <FormInput label="Carte 1" v-model="left" />
            <FormInput label="Carte 2" v-model="right" />
        </FormRow>
        <FormRow>
            <Column>
                <FormButton class="btn btn-primary float-end" :href="`/cards/pokemon/merge/${left},${right}`" :disabled="!valid">Fusioner</FormButton>
            </Column>
        </FormRow>
    </div>
</template>

<script lang="ts" setup>
import FormRow from "@components/form/FormRow.vue";
import FormInput from "@components/form/FormInput.vue";
import {ref} from "vue";
import {computedAsync} from "@vueuse/core";
import rest from "@/rest";
import Column from "@components/grid/Column.vue";
import FormButton from "@components/form/FormButton.vue";
import {usePageTitle} from "@/vue/composables/PageComposables";

usePageTitle("Pokémon - Fusion manuelle");

const left = ref("");
const right = ref("");

const valid = computedAsync<boolean>(async () => {
    if (!(parseInt(left.value) && parseInt(right.value) && left.value !== right.value)) {
        return false;
    }

    const [l, r] = await Promise.all([
        rest.get(`/api/cards/pokemon/${left.value}`),
        rest.get(`/api/cards/pokemon/${right.value}`)
    ]);

    return l && r;
});
</script>
