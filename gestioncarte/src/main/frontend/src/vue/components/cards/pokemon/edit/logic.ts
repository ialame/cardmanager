import {BracketDTO, FeatureDTO, PokemonCardDTO, PokemonSetDTO} from "@/types";
import {inject, ref, Ref, watchEffect} from "vue";
import {RestComposables} from "@/vue/composables/RestComposables";
import rest from "@/rest";
import {PokemonCardService} from "@/services/card/pokemon/PokemonCardService";
import {concat} from "lodash";
import {MaybeRefOrGetter, toRef, toValue} from "@vueuse/core";
import {LocalizationCode} from "@/localization";


export type EditedPokemonCardSpecialTranslation = {
    brackets: BracketDTO[],
    set?: PokemonSetDTO
};
export type EditedPokemonCard = Omit<PokemonCardDTO, 'brackets' | 'setIds' | 'featureIds'> & Partial<Record<LocalizationCode, EditedPokemonCardSpecialTranslation>> & {
    features: FeatureDTO[]
};

export const localizationsBoundToUS: LocalizationCode[]  = [ "fr", "de", "es", "it", "nl", "pt", "ru" ];
export const localizationsWithSet: LocalizationCode[]  = [ "us", "jp", "kr", "cn", "zh" ];

export function useParentCards(cardRef: MaybeRefOrGetter<PokemonCardDTO>) {
    const extractedCardList = inject<MaybeRefOrGetter<PokemonCardDTO[]>>("cardsList", []);
    const card = toRef(cardRef);

    return RestComposables.useRestComposable<PokemonCardDTO[]>(async () => {
        let list: PokemonCardDTO[] = [];

        if (card.value.distribution || card.value.alternate) {
            list = await rest.post(`/api/cards/pokemon/parent/search`, {data: card.value});
        }
        if (card.value.parentId && !list.some(c2 => c2.id === card.value.parentId)) {
            list.push(await rest.get(`/api/cards/pokemon/${card.value.parentId}`));
        }
        return list.concat(PokemonCardService.findParentCards(card.value, toValue(extractedCardList)));
    }, [], {watch: [card, () => toValue(extractedCardList)]});
}

export function useSavedCards(cardRef: MaybeRefOrGetter<PokemonCardDTO>, defaultSavedCardIds: MaybeRefOrGetter<string[]>, disabled: MaybeRefOrGetter<boolean>) {
    const savedCards = ref<PokemonCardDTO[]>([]) as Ref<PokemonCardDTO[]>;
    const cardsToSave = ref<PokemonCardDTO[]>([]) as Ref<PokemonCardDTO[]>;

    watchEffect(async () => {
        if (toValue(disabled)) {
            return;
        }

        const card = toValue(cardRef);
        const ids = toValue(defaultSavedCardIds);
        const found = (await rest.post(`/api/cards/pokemon/search`, { data: card }) as PokemonCardDTO[] || [])
            .filter(c => c.id !== card.id);
        const sc = concat(found, await Promise.all(ids
            .filter(id => !found.some(c => c.id === id))
            .map(id => rest.get(`/api/cards/pokemon/${id}`))));

        cardsToSave.value = sc.filter(c => !!c && ids.includes(c.id));
        savedCards.value = sc;
    });
    return { savedCards, cardsToSave };
}
