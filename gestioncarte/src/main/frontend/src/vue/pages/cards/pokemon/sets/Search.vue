<template>
    <PokemonSetSearch v-model="set" @submit="edit" :canEditSet="false" class="mt-3">
        <FormRow>
            <Column>
                <div class="d-flex flex-row float-end">
                    <FormButton color="primary" @click="edit">Modifier</FormButton>
                </div>
            </Column>
        </FormRow>
    </PokemonSetSearch>
</template>

<script lang="ts" setup>
import {PokemonSetDTO} from '@/types';
import FormButton from '@components/form/FormButton.vue';
import {ref} from 'vue';
import FormRow from '@components/form/FormRow.vue';
import Column from '@components/grid/Column.vue';
import {usePageTitle} from "@/vue/composables/PageComposables";
import {useRouter} from "vue-router";
import {PokemonSetSearch} from "@components/cards/pokemon/set";
import {useRaise} from "@/alert";

usePageTitle("Pokémon - Modification d'une extension");

const set = ref<PokemonSetDTO>();
const router = useRouter();
const raise = useRaise();
const edit = () => {
	if (set.value?.id) {
        router.push(`/cards/pokemon/sets/${set.value.id}`);
	} else {
		raise.warn("Aucune extension selectionée.");
	}
}
</script>
