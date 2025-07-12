import {arrayValidator, defaultValidator, ValidationRules, Validator} from "@/validation";
import {OnePieceCardDTO, PromoCardDTO} from "@/types";
import {promoCardValidationRules} from "@components/cards/promo";

const nameValidator: Validator<OnePieceCardDTO, string> = context => {
    if (!context.value) {
        return 'valid';
    }

    const capture = /(<\d+>)/.exec(context.value);

    if (capture) {
        return {
            status: 'invalid',
            message: `Remplacer ${capture[0]} par le nom parallel`,
            legendKeys: ['parallel'],
        }
    }
    return 'valid';
}

export const rules: ValidationRules<OnePieceCardDTO> = {
    number: {validators: [defaultValidator], required: 'optional'},
    rarity: {validators: [defaultValidator], required: 'optional'},
    color: {validators: [defaultValidator], required: 'optional'},
    type: {validators: [defaultValidator], required: 'optional'},
    parallel: {validators: [defaultValidator], required: 'optional'},
    artist: {validators: [defaultValidator], required: 'optional'},
    attribute: {validators: [defaultValidator], required: 'optional'},
    setIds: {
        validators: [arrayValidator(true)],
        required: 'optional',
        each: {
            validators: [defaultValidator],
            required: 'optional',
        }
    },
    translations: {
        required: 'optional',
        each: {
            name: { validators: [defaultValidator, nameValidator], required: 'optional'},
            labelName: { validators: [defaultValidator, nameValidator], required: 'optional'},
        },
    },
    promos: {
        validators: [arrayValidator<OnePieceCardDTO, PromoCardDTO>(true)],
        each: { mapped: promoCardValidationRules },
    }
};
