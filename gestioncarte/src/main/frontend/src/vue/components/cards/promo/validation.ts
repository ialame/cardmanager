import {defaultValidator, Requirement, ValidationRules} from "@/validation";
import {PromoCardDTO} from "@/types";

export const promoCardValidationRules: ValidationRules<PromoCardDTO> = {
    validators: [defaultValidator],
    comparator: (a: PromoCardDTO, b: PromoCardDTO) => a?.name === b?.name,
    name: {
        required: 'optional' as Requirement,
        validators: [defaultValidator],
    },
    required: 'optional' as Requirement,
};
