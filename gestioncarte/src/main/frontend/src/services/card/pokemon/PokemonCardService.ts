import {localizationCodes, Translations} from "@/localization";
import {PokemonCardDTO, PokemonCardTranslationDTO} from "@/types";
import {without} from "lodash";
import {merge} from "@/merge";

export namespace PokemonCardService {

    export const newPokemonCard = (): PokemonCardDTO => ({
        id: "",
        type: "",
        type2: "",
        brackets: [],
        featureIds: [],
        translations: {} as Translations<PokemonCardTranslationDTO>,
        setIds: [],
        promos: [],
        tags: [],
        fullArt: false
    });

    export const mergeCards = (card1: PokemonCardDTO, card2: PokemonCardDTO): PokemonCardDTO => {
        const card = merge([card1, card2], newPokemonCard());

        for (const localization of localizationCodes) {
            const t = card.translations[localization];
            if (t) {
                t.available = true;
            }
        }
        without(card.setIds, null, undefined);
        return card;
    };

    export const findParentCards = (card?: PokemonCardDTO, cards?: PokemonCardDTO[]): PokemonCardDTO[] => {
        if (!card || !cards || card.brackets.length === 0) {
            return [];
        }
        return cards.filter(c => c.brackets.length === 0 && card.setIds.some(s => c.setIds.includes(s)) && Object.values(card.translations).some(t => {
            const t2 = c.translations[t.localization];

            return t2 && t2.name === t.name && t2.number === t.number;
        }));
    }
}
