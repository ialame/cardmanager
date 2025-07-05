import {validateJson} from "@tu/validation/valisdationTestHelper";
import {createEditedPokemonCardRules} from "@components/cards/pokemon/edit";
import {getValidationStatus} from "@/validation";
import {describe, expect, it} from 'vitest';

describe('createEditedPokemonCardRules', () => {
    it('should be invalid if the card has a form but no form tag', async () => {
        const result = await validateJson(createEditedPokemonCardRules(), '20221219173828');

        expect(result).not.toBeNull();
        expect(result).not.toBeUndefined();
        expect(result['tags'].map(getValidationStatus)).toEqual(expect.arrayContaining(['invalid']));
    });
    it('should have invalid jp set', async () => {
        const result = await validateJson(createEditedPokemonCardRules(), '202212201679');

        expect(result).not.toBeNull();
        expect(result['jp.set'].map(getValidationStatus)).toEqual(expect.arrayContaining(['invalid']));
    });
    it('should not have invalid us name', async () => {
        const result = await validateJson(createEditedPokemonCardRules(), '20221222104721');

        expect(result).not.toBeNull();
        expect(result['translations.us.name'].map(getValidationStatus)).not.toEqual(expect.arrayContaining(['invalid']));
    });
});
