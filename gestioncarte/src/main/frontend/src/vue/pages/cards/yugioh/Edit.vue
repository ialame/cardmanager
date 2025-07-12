<template>
	<Loading :ready="ready">
		<div class="full-screen-container mt-4">
			<YuGiOhCardEditForm v-model="card" @save="save" :showMerge="false">
                <template #save-buttons>
                    <FormButton v-if="cardId === ''" color="secondary" @click="saveAndNew" class="me-2">Enregistrer et ajouter une nouvelle</FormButton>
                </template>
            </YuGiOhCardEditForm>
		</div>
	</Loading>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import YuGiOhCardEditForm from '@components/cards/yugioh/edit/YuGiOhCardEditForm.vue';
import {computed, ref, watchEffect} from 'vue';
import Loading from "@components/Loading.vue";
import {useRoute, useRouter} from "vue-router";
import FormButton from '@components/form/FormButton.vue';
import log from "loglevel";
import {useRaise} from "@/alert";
import {YuGiOhCardDTO} from "@/types";
import {usePageTitle} from "@/vue/composables/PageComposables";

const raise = useRaise();
const route = useRoute();
const router = useRouter();
const ready = ref(false);
const cardId = computed(() => route.params.cardId as string || '');
const card = ref<YuGiOhCardDTO>({
    id: '',
    types: [],
    translations: {},
    setIds: [],
    tags: [],
    idPrim: '',
    artist: '',
    level: 0,
    sneakPeek: false
});

const doSave = async () => {
    let id = cardId.value;

    log.info('Saving card', id);
    if (!id) {
        id = await rest.post("/api/cards/yugioh", {data: card.value});
    } else {
        await rest.put("/api/cards/yugioh", {data: card.value});
    }
    raise.success("Carte enregistrée avec succès.");
    return id;
};
const save = async () => {
    let id = await doSave();

    if (id !== cardId.value) {
        await router.push(`/cards/Yugioh/${id}`);
    }
};
const saveAndNew = async () => {
    await doSave();

    card.value = {
        id: '',
        types: [],
        translations: {},
        setIds: [],
        tags: [],
        idPrim: '',
        artist: '',
        level: 0,
        sneakPeek: false
    }
}


usePageTitle(computed(() => {
    const end = cardId.value ? ` (id: ${cardId.value})` : '';

    return `YuGiOh! - ${cardId.value ? 'Modification de carte' : 'Ajout manuel'}${end}`;
}));

watchEffect(async () => {
    if (cardId.value) {
        card.value = await rest.get(`/api/cards/yugioh/${cardId.value}`);
    }
    ready.value = true;
});
</script>
