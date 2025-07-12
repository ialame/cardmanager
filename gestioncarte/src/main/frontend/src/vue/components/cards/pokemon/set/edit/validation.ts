import {defaultValidator, ValidationContext, ValidationResult, ValidationRules} from "@/validation";
import {EditedPokemonSet} from "./logic";
import {localizationCodes} from "@/localization";
import rest from "@/rest";
import {ExpansionBulbapediaDTO} from "@/types";
import {getParentPath} from "@/path";
import {get} from "lodash";

async function expansionsBulbapediaUrlValidator(context: ValidationContext<EditedPokemonSet, string>): Promise<ValidationResult> {
    const id = get(context.data, getParentPath(context.path)).id;
    const expansions = (await rest.get("/api/cards/pokemon/bulbapedia/expansions", { params: { url: context.value }}) as ExpansionBulbapediaDTO[])
        .filter(exp => exp.url === context.value && id !== exp.id);

    return expansions.length > 0 ? ['warning', 'Cette URL est déjà utilisée pour une autre extension.'] : 'valid';
}

async function pokecardexCodeValidator(context: ValidationContext<EditedPokemonSet, string>): Promise<ValidationResult> {
    if (!context.value) {
        return 'valid';
    }

    const id = await rest.get(`/api/cards/pokemon/pokecardex/${context.value}/set-id`);

    if (id && id !== context.data.id) {
        return ['warning', 'Ce code est déjà utilisé pour une autre extension.'];
    }
    return 'valid';
}

async function pkmncardsComSetPathValidator(context: ValidationContext<EditedPokemonSet, string>): Promise<ValidationResult> {
    if (context.value && !context.value.startsWith('https://pkmncards.com/set/')) {
        return {
            status: 'invalid',
            message: 'L\'URL doit commencer par https://pkmncards.com/set/'
        };
    }

    return 'valid';
}

export const rulesParent: ValidationRules<EditedPokemonSet> = {
    serie: {validators: [defaultValidator], required: 'optional'},
    translations: {
        required: 'optional',
        each: {
            keys: localizationCodes,
            name: { validators: [defaultValidator], required: 'optional'},
            releaseDate: { validators: [defaultValidator], required: 'optional'},
            originalName: { validators: [defaultValidator], required: 'optional'},
        },
    }
};

export const rules: ValidationRules<EditedPokemonSet> = {
    ...rulesParent,
    parent: {validators: [defaultValidator], required: 'optional'},
    expansionsBulbapedia: {
        required: 'optional',
        name: { validators: [defaultValidator], required: 'optional'},
        list: {
            required: 'optional',
            each: {
                name: { validators: [defaultValidator], required: 'optional'},
                page2Name: { validators: [defaultValidator], required: 'optional'},
                firstNumber: { validators: [defaultValidator], required: 'optional'},
                tableName: { validators: [defaultValidator], required: 'optional'},
                url: { validators: [defaultValidator, expansionsBulbapediaUrlValidator], required: 'optional'},
                h3Name: { validators: [defaultValidator], required: 'optional'},
            }
        }
    },
    pokecardexCode: { validators: [defaultValidator, pokecardexCodeValidator], required: 'optional'},
    pkmncardsComSetPath: { validators: [defaultValidator, pkmncardsComSetPathValidator], required: 'optional'},
    japaneseOfficialSitePgs: {
        each: { validators: [defaultValidator], required: 'optional'}
    },
    officialSitePaths: {
        each: { validators: [defaultValidator], required: 'optional'}
    },
    ptcgoSets: {
        each: {
            fileName: { validators: [defaultValidator], required: 'optional'},
        }
    }
};
