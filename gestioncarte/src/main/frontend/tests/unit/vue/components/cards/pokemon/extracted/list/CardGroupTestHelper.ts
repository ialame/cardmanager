import {PokemonCardService} from "@/services/card/pokemon/PokemonCardService";
import {PokemonCardTranslationService} from "@/services/card/pokemon/PokemonCardTranslationService";
import {PokemonSetDTO} from "@/types";
import {PokemonSetService} from "@/services/card/pokemon/set/PokemonSetService";
import {ExtractedPokemonCardEntry} from "@components/cards/pokemon/extracted/list";

export namespace CardGroupTestHelper {
    export function mockCardGroup(group: number, index: number): ExtractedPokemonCardEntry {
        const card = PokemonCardService.newPokemonCard()

        card.id = "mock-card-id" + index;
        card.translations.us = PokemonCardTranslationService.create("us");
        card.translations.us.number = "" + group;
        card.translations.jp = PokemonCardTranslationService.create("jp");
        card.translations.jp.number = index + "/" + group;
        card.setIds = ["mock-set-id1"];
        return {
            extractedCard: {
                card: card,
                rawExtractedCard: card,
                id: index.toString(),
                parentCards: [],
                savedCards: [],
                translations: [],
                reviewed: false,
                bulbapediaStatus: "ok"
            },
            optionalPaths: [],
            reviewedPaths: [],
            status: [],
            validationResults: {},
        };
    }

    export const testSets: PokemonSetDTO[] = [
        (() => {
            const set = PokemonSetService.newSet();

            set.id = "mock-set-id1";
            set.translations.us = {
                available: true,
                localization: "us",
                name: "test",
                originalName: "",
                releaseDate: ""
            };
            return set;
        })()
    ];
}
