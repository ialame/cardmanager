<template>
    <hr />
    <AdvancedFormCollapse path="expansionsBulbapedia" label="Expensions Bulbapedia" class="card">
        <AdvancedFormInput v-if="value.list && value.list.length > 0" path="expansionsBulbapedia.name" label="Nom de l'extension sur Bulbapedia" />
        <AdvancedFormListInput path="expansionsBulbapedia.list" label="Expensions Bulbapedia" :initialValue="initialExtensionBulbapedia" >
            <template #default="{path: p}">
                <AdvancedFormCollapse :path="p" class="container-fluid w-100 card border border-3 mt-2" label="Expension Bulbapedia">
                    <FormRow>
                        <AdvancedFormInput :path="concatPaths([p, 'page2Name'])">
                            <template #label>
                                Nom&nbsp;<span class="fw-bold">en page 2</span>
                            </template>
                        </AdvancedFormInput>
                    </FormRow>
                    <FormRow>
                        <AdvancedFormInput :path="concatPaths([p, 'url'])">
                            <template #label>
                                URL&nbsp;<span class="fw-bold">de la page 2</span>
                            </template>
                        </AdvancedFormInput>
                    </FormRow>
                    <FormRow>
                        <AdvancedFormInput label="Nom du tableau" :path="concatPaths([p, 'tableName'])" />
                    </FormRow>
                    <FormRow class="mb-0">
                        <AdvancedFormInput label="Nom h3" :path="concatPaths([p, 'h3Name'])" />
                        <AdvancedFormInput label="Premier numero" :path="concatPaths([p, 'firstNumber'])" />
                    </FormRow>
                </AdvancedFormCollapse>
            </template>
        </AdvancedFormListInput>
    </AdvancedFormCollapse>
</template>

<script lang="ts" setup>
import {ExpansionBulbapediaDTO} from "@/types";
import {computed} from "vue";
import FormRow from "@components/form/FormRow.vue";
import {
    AdvancedFormCollapse,
    AdvancedFormInput,
    AdvancedFormListInput,
    useAdvancedFormInput
} from "@components/form/advanced";
import {concatPaths} from "@/path";
import {EditedPokemonSet, getInitialExtensionBulbapedia, getUsJp} from "./logic";

const {value, context} = useAdvancedFormInput<EditedPokemonSet, EditedPokemonSet['expansionsBulbapedia']>('expansionsBulbapedia');

const initialExtensionBulbapedia = computed((): ExpansionBulbapediaDTO => getInitialExtensionBulbapedia(value.value?.name, getUsJp(context.value.data)));
</script>

<style lang="scss" scoped>
@import "src/vue/components/form/advanced/AdvancedForm";

.card {
    :deep(>div>.form-label) {
        @include bold-label;
        margin-bottom: 0.5rem;
    }
}
</style>
