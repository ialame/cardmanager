import {arrayValidator, defaultValidator, ValidationRules} from "@/validation";
import {LorcanaCardDTO} from "@/types";

export const rules: ValidationRules<LorcanaCardDTO> = {
    rarity: {validators: [defaultValidator], required: 'optional'},
    artist: {validators: [defaultValidator], required: 'optional'},
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
            name: { validators: [defaultValidator], required: 'optional'},
            labelName: { validators: [defaultValidator], required: 'optional'},
            number: {validators: [defaultValidator], required: 'optional'},
        },
    }
};
