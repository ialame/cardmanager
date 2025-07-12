import {defaultValidator, ValidationRules} from "@/validation";
import {LorcanaSetDTO} from "@/types";

export const rules: ValidationRules<LorcanaSetDTO> = {
    number: {validators: [defaultValidator], required: 'optional'},
    translations: {
        required: 'optional',
        each: {
            name: { validators: [defaultValidator], required: 'optional'},
        },
    }
};
