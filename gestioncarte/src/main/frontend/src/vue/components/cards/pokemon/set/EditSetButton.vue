<template>
    <FormButton color="secondary" title="Modifier l'extension" @click="editSet(id)" :disabled="!id">
        <Icon name="pencil" />
    </FormButton>
</template>

<script lang="ts" setup>
import FormButton from '@components/form/FormButton.vue';
import Icon from "@components/Icon.vue";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";

interface Props {
    id?: string;
}

withDefaults(defineProps<Props>(), {  id: '', });

const editSet = PokemonComposables.useOpenEditSet();
</script>
