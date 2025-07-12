<template>
	<Loading :ready="ready">
		<div class="full-screen-container mt-4">
			<LorcanaCardEditForm v-model="card" @save="save" :showMerge="false">
                <template #save-buttons>
                    <FormButton v-if="cardId === ''" color="secondary" @click="saveAndNew" class="me-2 mt-4 mb-4">Enregistrer et ajouter une nouvelle</FormButton>
                </template>
            </LorcanaCardEditForm>
		</div>
	</Loading>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import LorcanaCardEditForm from '@components/cards/lorcana/edit/LorcanaCardEditForm.vue';
import {computed, ref, watchEffect} from 'vue';
import Loading from "@components/Loading.vue";
import {useRoute, useRouter} from "vue-router";
import FormButton from '@components/form/FormButton.vue';
import log from "loglevel";
import {useRaise} from "@/alert";
import {useLorcanaTitle} from "@components/cards/lorcana/logic";
import {LorcanaCardDTO} from "@/types";

const raise = useRaise();
const route = useRoute();
const router = useRouter();
const ready = ref(false);
const cardId = computed(() => route.params.cardId as string || '');
const fromId = computed(() => route.query.from as string || '');
const card = ref<LorcanaCardDTO>({
    id: '',
    translations: {},
    setIds: [],
    tags: [],
    idPrim: '',
    artist: '',
    rarity: '',
    ink: '',
    reprint: false
});

const doSave = async () => {
    let id = cardId.value;

    log.info('Saving card', id);
    if (!id) {
        id = await rest.post("/api/cards/lorcana", {data: card.value});
    } else {
        await rest.put("/api/cards/lorcana", {data: card.value});
    }
    raise.success("Carte enregistrée avec succès.");
    return id;
};
const save = async () => {
    let id = await doSave();

    if (id !== cardId.value) {
        await router.push(`/cards/lorcana/${id}`);
    }
};
const saveAndNew = async () => {
    await doSave();

    card.value = {
        id: '',
        translations: {},
        setIds: [],
        tags: [],
        idPrim: '',
        artist: '',
        rarity: '',
        ink: '',
        reprint: false
    }
}


useLorcanaTitle(computed(() => {
    const end = cardId.value ? ` (id: ${cardId.value})` : '';

    return `${cardId.value ? 'Modification de carte' : 'Ajout manuel'}${end}`;
}));

watchEffect(async () => {
    if (cardId.value) {
        card.value = await rest.get(`/api/cards/lorcana/${cardId.value}`);
    } else if (fromId.value) {
        const c = await rest.get(`/api/cards/lorcana/${fromId.value}`);

        c.id = '';
        card.value = c;
    }
    ready.value = true;
});
</script>
