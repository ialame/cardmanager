<template>
    <FormRow>
        <AdvancedFormSelect path="type" label="Type" :values="types"/>
        <AdvancedFormSelect path="type2" label="Type2" :values="types"/>
        <AdvancedFormInput path="level" type="number" label="Niveau"/>
    </FormRow>
    <FormRow class="align-items-end">
        <Column class="form-group">
            <AdvancedFormInput path="artist" label="Artiste"/>
        </Column>
        <Column class="mb-2">
            <AdvancedFormCheckbox path="fullArt" label="Full art"/>
            <AdvancedFormCheckbox path="distribution" label="Distribution"/>
            <AdvancedFormCheckbox path="alternate" label="Alternate"/>
        </Column>
    </FormRow>
    <FormRow>
        <AdvancedFormListInput path="features" label="Particularités" #default="{ path }">
            <AdvancedFormSelect :path="path" :values="features" #default="{ value: feature }">
                {{feature?.pcaName || ''}}  <IdTooltip :id="feature?.id"/>
            </AdvancedFormSelect>
        </AdvancedFormListInput>
    </FormRow>
    <CardTagListInput />
</template>

<script lang="ts" setup>
import FormRow from "@components/form/FormRow.vue";
import {
    AdvancedFormCheckbox,
    AdvancedFormInput,
    AdvancedFormListInput,
    AdvancedFormSelect
} from "@components/form/advanced";
import IdTooltip from "@components/tooltip/IdTooltip.vue";
import Column from "@components/grid/Column.vue";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {CardTagListInput} from "@components/cards/tags";

const types = PokemonComposables.usePokemonCardTypes();
const features = PokemonComposables.pokemonFeatureService.all;

</script>
