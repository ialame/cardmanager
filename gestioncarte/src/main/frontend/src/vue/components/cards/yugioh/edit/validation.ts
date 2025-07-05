import {defaultValidator, ValidationRules, Validator} from "@/validation";
import {YuGiOhCardDTO} from "@/types";
import {localizationCodes} from "@/localization";

const updatedFromValidator: Validator<YuGiOhCardDTO, string> = context => {
    const text = context.value;

    if (!text) {
        return 'valid';
    }

    if (text.toLowerCase().includes('updated from')) {
        return {
            status: 'invalid',
            message: 'Le nom ne doit pas contenir "updated from"'
        }
    }
    return 'valid';
}

export const yugiohCardRules: ValidationRules<YuGiOhCardDTO> = {
    types: {
        each: { validators: [defaultValidator], required: 'optional' }
    },
    translations: {
        each: {
            keys: localizationCodes,
            name: { validators: [defaultValidator, updatedFromValidator], required: 'optional' },
            labelName: { validators: [defaultValidator, updatedFromValidator], required: 'optional' },
        }
    }
}
