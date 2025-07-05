<template>
	<Loading :ready="ready">
		<div class="full-screen-container mt-4">
			<YuGiOhCardEditForm v-model="card" @save="save" :mergeSources="mergeSources" />
		</div>
	</Loading>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import {MergeSourceArray, YuGiOhCardDTO} from '@/types';
import {ref, watchEffect} from 'vue';
import {isEmpty, isNil, without} from "lodash";
import {localizationCodes} from "@/localization";
import {ConfirmComposables} from "@/vue/composables/ConfirmComposables";
import {useRouter} from "vue-router";
import Loading from "@components/Loading.vue";
import {useCommaSeparatedParam, usePageTitle} from "@/vue/composables/PageComposables";
import {useRaise} from "@/alert";
import YuGiOhCardEditForm from "@components/cards/yugioh/edit/YuGiOhCardEditForm.vue";
import {merge} from "@/merge";

usePageTitle("YuGiOh! - Fusion manuelle");

const router = useRouter();
const raise = useRaise();
const ready = ref(false);
const mergeSources = ref<MergeSourceArray<YuGiOhCardDTO>>([]);

const cardIds = useCommaSeparatedParam('cardIds');
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

const save = async () => {
    if (!card.value) {
        raise.warn("Aucune cartes existantes a marier!");
        return;
    }
    if (!card.value.id) {
        card.value.id = cardIds.value[0];
    }
    await rest.put(`/api/cards/yugioh/${cardIds.value.join(',')}/merge`, { data: card.value });
    raise.success("Carte enregistrée avec succès.");
    await router.push(`/cards/yugioh/${cardIds.value[0]}`);
};

const equalsOrEmpty = (a: any, b: any) => isNil(a) || isNil(b) || isEmpty(a) || isEmpty(b) || a === b;

// TODO move to a service
const mergeCards = (card1: YuGiOhCardDTO, card2: YuGiOhCardDTO): YuGiOhCardDTO => {
    const card = merge([card1, card2], {
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
    
    for (const localization of localizationCodes) {
        const t = card.translations[localization];
        if (t) {
            t.available = true;
        }
    }
    without(card.setIds, null, undefined);
    return card;
};

watchEffect(async () => {
    const cards: YuGiOhCardDTO[] = await Promise.all(cardIds.value.map(id => rest.get(`/api/cards/yugioh/${id}`)));

    if (cards.length > 1) {
        if (cards.some(isNil)) {
            raise.error("Une ou plusieurs cartes n'ont pas pu être chargées.");
            await router.push('/cards/yugioh/new');
            return;
        }

        if (localizationCodes.some(l => !equalsOrEmpty(cards[0].translations[l]?.number, cards[1]?.translations[l]?.number)) && !await ConfirmComposables.confirmOrCancel("Deux cartes avec des numéros différents ont été sélectionnées. Continuer quand même?")) {
            await router.push(`/cards/yugioh/${cardIds.value[0]}/edit`);
            return;
        } else {
            card.value = mergeCards(cards[0], cards[1]);
            mergeSources.value = [cards[0], cards[1]];
        }
    } else if (cards.length === 1) {
        await router.push(`/cards/yugioh/${cardIds.value[0]}/edit`);
        return;
    }
    ready.value = true;
});
</script>
