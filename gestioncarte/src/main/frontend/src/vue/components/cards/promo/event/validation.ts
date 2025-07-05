import {arrayValidator, defaultValidator, ValidationRules} from "@/validation";
import {PromoCardEventDTO, PromoCardEventTraitDTO} from "@/types";
import {localizationCodes} from "@/localization";

export const promoCardEventValidationRules: ValidationRules<PromoCardEventDTO> = {
    name: {
        validators: [defaultValidator],
        required: 'optional'
    },
    traits: {
        validators: [arrayValidator<PromoCardEventDTO, PromoCardEventTraitDTO>(true)],
        each: {
            required: 'optional',
            validators: [defaultValidator],
        }
    },
    translations: {
        required: 'optional',
        each: {
            keys: localizationCodes,
            name: { validators: [defaultValidator], required: 'optional' },
            releaseDate: { validators: [defaultValidator], required: 'optional' },
        },
    },
};
