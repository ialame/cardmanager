import {PokemonSetService} from "@/services/card/pokemon/set/PokemonSetService";
import {describe, expect, it} from 'vitest';

describe('PokemonSetService.newSet', () => {
    it('returns a new pokemon set', () => {
        let set = PokemonSetService.newSet();

        expect(set.id).toMatch("");
    });
});

describe('PokemonSetService.newEditSetForm', () => {
    it('returns a new edit set form', () => {
        let form = PokemonSetService.newEditSetForm();

        expect(form.set.id).toMatch("");
    })
});
