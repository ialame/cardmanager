import {defaultValidator, ValidationContext, ValidationRules, Validator} from "@/validation";
import {EditedYuGiOhSet, EditedYuGiOhSetTranslation} from "@components/cards/yugioh/set";
import {getParentPath} from "@/path";
import {get} from "lodash";
import rest from "@/rest";
import {Translations} from "@/localization";

export const pidValidator: Validator<EditedYuGiOhSet, string> = async (context: ValidationContext<EditedYuGiOhSet, string>) => {
    const parent: EditedYuGiOhSetTranslation = get(context.data, getParentPath(context.path, 2));
    const name = (await rest.get(`/api/cards/yugioh/official-site/sets/${context.value}/${parent.localization}/name`, { raw: true })) as string;

    if (!name) {
        return ['invalid', "Ce PID n'existe pas sur le site officiel."];
    } else if (name.toLowerCase() !== parent.name.toLowerCase()) {
        return {
            status: 'invalid',
            message: `Le nom de ce PID (${name}) ne correspond pas au nom de l'extension (${parent.name}).`,
            reviewable: true,
        }
    }
    return 'valid';
}
export const isolatedCodeValidator: Validator<EditedYuGiOhSet, string> = async (context: ValidationContext<EditedYuGiOhSet, string>) => {
    if (!context.value) {
        return 'valid';
    }

    const parent: EditedYuGiOhSetTranslation = get(context.data, getParentPath(context.path));
    const translations: Translations<EditedYuGiOhSetTranslation> = get(context.data, getParentPath(context.path, 2));

    if (!translations) {
        return 'valid';
    } else if (!Object.values(translations).some(t => t.localization !== parent.localization && t.code === context.value)) {
        return ['warning', `Le code ${context.value} n'est utilisé que par cette langue.`];
    }
    return 'valid';
}
export const yugiohSetRules: ValidationRules<EditedYuGiOhSet> = {
    serie: { validators: [defaultValidator], required: 'optional' },
    translations: {
        required: 'optional',
        each: {
            code: { validators: [defaultValidator, isolatedCodeValidator], required: 'optional' },
            releaseDate: { validators: [defaultValidator], required: 'optional' },
            officialSitePids: {
                required: 'optional',
                each: { validators: [defaultValidator, pidValidator], required: 'optional' }
            },
            yugipediaSets: {
                required: 'optional',
                each: { validators: [defaultValidator], required: 'optional' }
            }
        }
    }
}
