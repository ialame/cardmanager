<template>
	<Loading :ready="ready">
		<div class="full-screen-container mt-4">
			<PokemonCardEditForm v-model="card" @save="save" :defaultSavedCardIds="cardIds" :diffSource="mergeSources" />
		</div>
	</Loading>
</template>

<script lang="ts" setup>
import rest from '@/rest';
import {MergeSourceArray, PokemonCardDTO} from '@/types';
import {ref, watchEffect} from 'vue';
import {PokemonCardService} from "@/services/card/pokemon/PokemonCardService";
import {isEmpty, isNil} from "lodash";
import {localizationCodes} from "@/localization";
import {ConfirmComposables} from "@/vue/composables/ConfirmComposables";
import {useRouter} from "vue-router";
import Loading from "@components/Loading.vue";
import {useCommaSeparatedParam, usePageTitle} from "@/vue/composables/PageComposables";
import {useRaise} from "@/alert";
import {PokemonCardEditForm} from "@components/cards/pokemon/edit";

usePageTitle("Pokémon - Fusion manuelle");

const router = useRouter();
const raise = useRaise();
const ready = ref(false);
const mergeSources = ref<MergeSourceArray<PokemonCardDTO>>([]);

const cardIds = useCommaSeparatedParam('cardIds');
const card = ref<PokemonCardDTO>(PokemonCardService.newPokemonCard());

const save = async () => {
    if (!card.value) {
        raise.warn("Aucune cartes existantes a marier!");
        return;
    }
    if (!card.value.id) {
        card.value.id = cardIds.value[0];
    }
    await rest.put(`/api/cards/pokemon/${cardIds.value.join(',')}/merge`, { data: card.value });
    raise.success("Carte enregistrée avec succès.");
    await router.push(`/cards/pokemon/${cardIds.value[0]}`);
};

const equalsOrEmpty = (a: any, b: any) => isNil(a) || isNil(b) || isEmpty(a) || isEmpty(b) || a === b;

watchEffect(async () => {
    const cards: PokemonCardDTO[] = await Promise.all(cardIds.value.map(id => rest.get(`/api/cards/pokemon/${id}`)));

    if (cards.length > 1) {
        if (cards.some(isNil)) {
            raise.error("Une ou plusieurs cartes n'ont pas pu être chargées.");
            await router.push('/cards/pokemon/new');
            return;
        }

        if (localizationCodes.some(l => !equalsOrEmpty(cards[0].translations[l]?.number, cards[1]?.translations[l]?.number)) && !await ConfirmComposables.confirmOrCancel("Deux cartes avec des numéros différents ont été sélectionnées. Continuer quand même?")) {
            await router.push(`/cards/pokemon/${cardIds.value[0]}/edit`);
            return;
        } else {
            card.value = PokemonCardService.mergeCards(cards[0], cards[1]);
            mergeSources.value = [cards[0], cards[1]];
        }
    } else if (cards.length === 1) {
        await router.push(`/cards/pokemon/${cardIds.value[0]}/edit`);
        return;
    }
    ready.value = true;
});
</script>
