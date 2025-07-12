import {PokemonCardService} from "@/services/card/pokemon/PokemonCardService";
import {PokemonCardDTO} from "@/types";
import jolteonDB1 from '@tr/card/pokemon/jolteon-db1.json';
import jolteonDB2 from '@tr/card/pokemon/jolteon-db2.json';
import {describe, expect, it} from 'vitest';

describe('PokemonCardService.newPokemonCard', () => {
    it('returns a new pokemon card', () => {
        let card = PokemonCardService.newPokemonCard();

        expect(card.id).toMatch("");
    });
});

describe('PokemonCardService.mergeCards', () => {
    it('merge 2 cards without errors', () => {
        // @ts-ignore
        let card = PokemonCardService.mergeCards(jolteonDB1 as PokemonCardDTO, jolteonDB2 as PokemonCardDTO);

        expect(card).not.toBeNull();
        expect(card).not.toBeUndefined();
        expect(card.setIds).not.toEqual(expect.arrayContaining([null]));
        expect(card.setIds).not.toEqual(expect.arrayContaining([undefined]));
        expect(card.setIds).not.toEqual(expect.arrayContaining(['']));
    });
});
