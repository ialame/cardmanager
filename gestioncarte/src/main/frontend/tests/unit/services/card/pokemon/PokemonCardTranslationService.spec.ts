import {PokemonCardTranslationService} from "@/services/card/pokemon/PokemonCardTranslationService";
import {describe, expect, it} from 'vitest';


describe('PokemonCardTranslationService.newPokemonCard', () => {
    it('returns a new pokemon card', () => {
        let card = PokemonCardTranslationService.create('us');

        expect(card.localization).toMatch("us");
        expect(card.number).toMatch("");
    });
});
