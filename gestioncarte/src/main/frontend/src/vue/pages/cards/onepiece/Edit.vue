<template>
	<Loading :ready="ready">
		<div class="full-screen-container mt-4">
			<OnePieceCardEditForm v-model="card" @save="save" :showMerge="false">
                <template #save-buttons>
                    <FormButton v-if="cardId === ''" color="secondary" @click="saveAndNew" class="me-2 mt-4 mb-4">Enregistrer et ajouter une nouvelle</FormButton>
                </template>
            </OnePieceCardEditForm>
		</div>
	</Loading>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import {computed, ref, watchEffect} from 'vue';
import Loading from "@components/Loading.vue";
import {useRoute, useRouter} from "vue-router";
import FormButton from '@components/form/FormButton.vue';
import log from "loglevel";
import {useRaise} from "@/alert";

import {OnePieceCardDTO} from "@/types";
import OnePieceCardEditForm from "@components/cards/onepiece/edit/OnePieceCardEditForm.vue";
import {useOnePieceTitle} from "@components/cards/onepiece/logic";

const raise = useRaise();
const route = useRoute();
const router = useRouter();
const ready = ref(false);
const cardId = computed(() => route.params.cardId as string || '');
const fromId = computed(() => route.query.from as string || '');
const card = ref<OnePieceCardDTO>({
    id: '',
    number: '',
    type: '',
    attribute: '',
    color: '',
    rarity: '',
    translations: {},
    setIds: [],
    tags: []
});

const doSave = async () => {
    let id = cardId.value;

    log.info('Saving card', id);
    if (!id) {
        id = await rest.post("/api/cards/onepiece", {data: card.value});
    } else {
        await rest.put("/api/cards/onepiece", {data: card.value});
    }
    raise.success("Carte enregistrée avec succès.");
    return id;
};
const save = async () => {
    let id = await doSave();

    if (id !== cardId.value) {
        await router.push(`/cards/onepiece/${id}`);
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


useOnePieceTitle(computed(() => {
    const end = cardId.value ? ` (id: ${cardId.value})` : '';

    return `${cardId.value ? 'Modification de carte' : 'Ajout manuel'}${end}`;
}));

watchEffect(async () => {
    if (cardId.value) {
        card.value = await rest.get(`/api/cards/onepiece/${cardId.value}`);
    } else if (fromId.value) {
        const c = await rest.get(`/api/cards/onepiece/${fromId.value}`);

        c.id = '';
        card.value = c;
    }
    ready.value = true;
});
</script>
