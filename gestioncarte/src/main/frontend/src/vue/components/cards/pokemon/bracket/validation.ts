import {defaultValidator, ValidationContext, ValidationResult, ValidationRules} from "@/validation";
import {BracketDTO} from "@/types";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {localizationCodes} from "@/localization";

async function bracketNameValidator(context: ValidationContext<BracketDTO, string>): Promise<ValidationResult> {
    if (context.value && !(await PokemonComposables.bracketService.findAllByName(context.value)).some(b => b?.id === context.data?.id)) {
        return ['warning', 'Un crochet existe déjà avec ce nom'];
    } else if (context.value?.toLowerCase().includes('form')) {
        return ['invalid', 'Les crochets de forme sont dépréciés. Utiliser les tags pour les formes'];
    }
    return 'valid';
}

export const bracketValidationRules: ValidationRules<BracketDTO> = {
    name: {
        validators: [defaultValidator, bracketNameValidator],
        required: 'optional'
    },
    translations: {
        required: 'optional',
        each: {
            keys: localizationCodes,
            name: { validators: [defaultValidator], required: 'optional' },
        },
    },
};
