<template>
    <FormRow v-if="!isParent && filteredSets.length > 0">
        <div class="d-flex flex-row">
            <PokemonSetSelect path="parent" label="Extension mere" :values="filteredSets" advanced />
            <div v-if="!readOnly" class="ms-2 d-flex flex-column">
                <FormButton color="danger" class="form-btn" @click="remove"><Icon name="trash" /></FormButton>
            </div>
        </div>
    </FormRow>
</template>

<script lang="ts" setup>
import FormRow from "@components/form/FormRow.vue";
import {computedAsync} from "@vueuse/core";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {EditedPokemonSet, PokemonSetSelect} from "@components/cards/pokemon/set";
import FormButton from "@components/form/FormButton.vue";
import Icon from "@components/Icon.vue";
import {useAdvancedFormInput} from "@components/form/advanced";
import {PokemonSetDTO} from "@/types";

interface Props {
    isParent?: boolean;
}

withDefaults(defineProps<Props>(), {
    isParent: false,
});

const {value, readOnly, context} = useAdvancedFormInput<EditedPokemonSet, PokemonSetDTO>("parent");

const filteredSets = computedAsync(async () => {
    const serieId = context.value.data?.serie?.id;

    return serieId ? (await PokemonComposables.pokemonSetService.find(s => s.serieId === serieId)) : [];
}, []);
const remove = () => value.value = undefined;
</script>

<style lang="scss" scoped>
div.d-flex .flex-column {
    margin-top: auto !important;

    .form-btn {
        margin-bottom: 1px !important;
    }
}
</style>
