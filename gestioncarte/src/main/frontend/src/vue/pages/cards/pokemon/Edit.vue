<template>
	<Loading :ready="ready">
		<div class="full-screen-container mt-4">
			<PokemonCardEditForm v-model="card" @save="save" :showMerge="false">
                <template #save-buttons>
                    <FormButton v-if="cardId === ''" color="secondary" @click="saveAndNew" class="me-2">Enregistrer et ajouter une nouvelle</FormButton>
                </template>
            </PokemonCardEditForm>
		</div>
	</Loading>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import {PokemonCardDTO} from '@/types';
import {PokemonCardEditForm} from '@components/cards/pokemon/edit';
import {computed, ref, watchEffect} from 'vue';
import {cloneDeep} from "lodash";
import {PokemonCardService} from "@/services/card/pokemon/PokemonCardService";
import {usePageTitle} from "@/vue/composables/PageComposables";
import Loading from "@components/Loading.vue";
import {useRoute, useRouter} from "vue-router";
import FormButton from '@components/form/FormButton.vue';
import log from "loglevel";
import {useRaise} from "@/alert";

const raise = useRaise();
const route = useRoute();
const router = useRouter();
const ready = ref(false);
const cardId = computed(() => route.params.cardId as string || '');
const card = ref(PokemonCardService.newPokemonCard());

const doSave = async () => {
    let id = cardId.value;

    log.info('Saving card', id);
    if (!id) {
        id = await rest.post("/api/cards/pokemon", {data: card.value});
    } else {
        await rest.put("/api/cards/pokemon", {data: card.value});
    }
    raise.success("Carte enregistrée avec succès.");
    return id;
};
const save = async () => {
    let id = await doSave();

    if (id !== cardId.value) {
        await router.push(`/cards/pokemon/${id}/edit`);
    }
};
const saveAndNew = async () => {
    await doSave();

    const parentId = route.query['from-parent'];

    if (parentId) {
        await loadFromParent(parentId as string);
    } else {
        card.value = PokemonCardService.newPokemonCard();
    }
}


usePageTitle(computed(() => {
    const end = cardId.value ? ` (id: ${cardId.value})` : '';

    return `Pokémon - ${cardId.value ? 'Modification de carte' : 'Ajout manuel'}${end}`;
}));

const loadFromParent = async (parentId: string) => {
    const parent = await rest.get(`/api/cards/pokemon/${parentId}`) as PokemonCardDTO;
    const value = cloneDeep(parent);

    value.id = '';
    value.distribution = true;
    value.parentId = parent.id;
    card.value = value;
}

watchEffect(async () => {
    const parentId = route.query['from-parent'];

    if (parentId) {
        await loadFromParent(parentId as string);
    } else if (cardId.value) {
        card.value = await rest.get(`/api/cards/pokemon/${cardId.value}`);
    }
    ready.value = true;
});
</script>
