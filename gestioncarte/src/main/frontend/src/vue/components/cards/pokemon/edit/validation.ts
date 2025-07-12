import {
    BracketDTO,
    CardTagDTO,
    FeatureDTO,
    PokemonCardDTO,
    PokemonCardTranslationDTO,
    PokemonSetDTO,
    PromoCardDTO,
    SourcedPokemonCardTranslationDTO
} from "@/types";
import {chain, cloneDeep, filter, find, set, toPath} from "lodash";
import {PokemonComposables} from "@/vue/composables/pokemon/PokemonComposables";
import {
    arrayValidator,
    createValidationContext,
    defaultValidator,
    getStrongestValidationStatus,
    getValidationStatus,
    Requirement,
    validatePath,
    ValidationContext,
    ValidationResult,
    ValidationRules
} from "@/validation";
import {createGlobalState, invoke, MaybeRefOrGetter, toValue} from "@vueuse/core";
import {
    EditedPokemonCard,
    EditedPokemonCardSpecialTranslation,
    localizationsWithSet
} from "@components/cards/pokemon/edit";
import {computed} from "vue";
import {LocalizationCode, localizationCodes, localizations, sortLocalizations, Translations} from "@/localization";
import {bracketValidationRules} from "@components/cards/pokemon/bracket";
import {Path, pathEndsWith} from "@/path";
import {getSetLocalizations as getSetLocalizationsFromIds} from "@components/cards/pokemon/set";
import {pokemonCardTranslatorService} from "@components/cards/pokemon/translators";
import {promoCardValidationRules} from "@components/cards/promo/validation";
import pokemonFeatureService = PokemonComposables.pokemonFeatureService;

const nameMatchRegex = /[{}[\]]/;

function isTranslationValid(card: EditedPokemonCard | PokemonCardDTO, path: Path) {
    return !!card.translations[toPath(path)[1] as LocalizationCode]?.available;
}

const useNameValidator = createGlobalState(() => {
    async function getFeatures(context: ValidationContext<EditedPokemonCard | PokemonCardDTO, string>): Promise<FeatureDTO[]> {
        if ('features' in context.data) {
            return context.data.features;
        } else if ('featureIds' in context.data) {

            return (await Promise.all(context.data.featureIds
                .map(async id => await pokemonFeatureService.get(id))))
                .filter(f => f);
        } else {
            return [];
        }
    }

    function isFeatureNamePresent(name: string, verificationPattern: string | undefined, featureName: string): boolean {
        if (verificationPattern) {
            return new RegExp(verificationPattern, 'i').test(name);
        }
        return featureName === '' || name.includes(featureName);
    }

    return async (context: ValidationContext<EditedPokemonCard | PokemonCardDTO, string>): Promise<ValidationResult> => {
        if (!isTranslationValid(context.data, context.path) || !context.value) {
            return 'valid';
        } else if (nameMatchRegex.exec(context.value)) {
            return ['invalid', 'Le nom ne doit pas contenir d\'adcolade ou de crochet.'];
        }

        const pathSplit = toPath(context.path);

        if (!pathSplit || pathSplit.length <= 0) {
            return 'valid';
        }

        const featureProp = pathSplit[pathSplit.length - 1] as 'name' | 'labelName';
        const localization = pathSplit[pathSplit.length - 2] as LocalizationCode;
        const features = await getFeatures(context);

        if (features && features.length > 0) {
            for (const feature of features) {
                const featureTranslation = feature.translations?.[localization];

                if (featureTranslation) {
                    const featureName = featureTranslation?.[featureProp === 'labelName' ? 'zebraName' : featureProp] || '';

                    if (!isFeatureNamePresent(context.value, featureProp === 'name' ? featureTranslation?.verificationPattern : featureTranslation?.labelVerificationPattern, featureName)) {
                        return ['invalid', `La particularité ${feature.pcaName}${featureName ? ' (' + featureName + ')' : ''} n'a pas été trouvée.`];
                    }
                }
            }
        }
        return 'valid';
    };
});

type ParsedSource = {value: any, source: string, blocking: boolean};
function compareSources(t1: ParsedSource, t2: ParsedSource) {

    if (t1.source === 'official-site-jp' || t2.source === 'official-site-jp') {
        return t1.value.replace(/\s/g,'') === t2.value.replace(/\s/g,'')
    }
    return t1.value === t2.value;
}
function useSourceTranslationValidator(sourceTranslations: MaybeRefOrGetter<SourcedPokemonCardTranslationDTO[]>) {

    return async (context: ValidationContext<PokemonCardDTO | EditedPokemonCard, string>): Promise<ValidationResult> => {
        if (context.reviewed || !isTranslationValid(context.data, context.path)) {
            return 'valid';
        }

        const sourceTranslationValues = toValue(sourceTranslations);
        const pathSplit = toPath(context.path);

        if (!pathSplit || pathSplit.length <= 0) {
            return 'valid';
        }

        const target = pathSplit[pathSplit.length - 1] as keyof PokemonCardTranslationDTO;
        const localization = pathSplit[pathSplit.length - 2] as LocalizationCode;

        const valueMap = (await Promise.all(sourceTranslationValues.filter(st => st.localization === localization)
            .map(async st => {
                const val = st.translation[target];
                const translator = (await pokemonCardTranslatorService.find(t => t.code === st.source))[0];

                return {value: val, source: st.source, blocking: !!(translator?.blocking && val)};
            }))).filter(v => !!v.value) as ParsedSource[];

        if (filter(valueMap, (t1, _i, iteratee) => find(iteratee, t2 => t1.blocking && t2.blocking && !compareSources(t1, t2))).length > 0) {
            return {
                status: 'invalid',
                message: 'Les traductions sont en conflit bloquant',
                reviewable: true,
                legendKeys: ['translation-conflict'],
                additionalInfo: { sourceTranslationConflict: 'blocking' }
            };
        } else if (filter(valueMap, (t1, _i, iteratee) => find(iteratee, t2 => !compareSources(t1, t2))).length > 0) {
            return {
                status: 'warning',
                message: 'Les traductions sont en conflit non bloquant',
                additionalInfo: { sourceTranslationConflict: 'non-blocking' }
            }
        }
        return 'valid';
    };
}

const pokemonWithForme = ['ditto', 'deoxys', 'castform'];
function tagsValidator(context: ValidationContext<PokemonCardDTO | EditedPokemonCard, CardTagDTO[]>): ValidationResult {
    if (context.reviewed) {
        return 'valid';
    }

    const data = context.data;
    const names = Object.values(data.translations)
        .map(t => t?.name?.toLowerCase())
        .filter(n => !!n);

    if (data.tags?.some(t => t.type === 'forme')) {
        return "valid";
    } else if (pokemonWithForme.some(f => names.some(n => n.includes(f)))) {
        return {
            status: 'invalid',
            message: 'Les Pokémons tels que Ditto, Castform, Deoxys, etc. doivent avoir une forme.',
            reviewable: true
        };
    } else if (names.some(n => n.includes('form'))) {
        return {
            status: 'invalid',
            message: 'La carte a une Forme mais n\'a pas de tag associé.',
            reviewable: true
        }
    }
    return 'valid';
}
function tagValidator(context: ValidationContext<PokemonCardDTO | EditedPokemonCard, CardTagDTO>): ValidationResult {
    const tag = context.value;

    if (!tag?.id) {
        const cardLocalizations = sortLocalizations(Object.keys(context.data.translations) as LocalizationCode[]);
        const tagLocalizations = sortLocalizations(Object.keys(tag?.translations ?? {}) as LocalizationCode[]);
        const missingLocalizations = cardLocalizations
            .filter(l => !tagLocalizations.includes(l))
            .map(l => localizations[l].name);

        if (missingLocalizations.length > 0) {
            return ['warning', `Le tag n'a pas de traduction pour les localisations ${missingLocalizations.join(', ')}`];
        }
    }
        return 'valid';
}
function parentIdValidator(context: ValidationContext<PokemonCardDTO | EditedPokemonCard, string>): ValidationResult {
    if (context.value && context.value === context.data.id) {
        return ['invalid', 'Une carte ne peut pas être son propre parent.'];
    }
    return 'valid';
}

function promosValidator(context: ValidationContext<PokemonCardDTO | EditedPokemonCard, PromoCardDTO[]>): ValidationResult {
    if (context.data.distribution && !context.value?.length) {
        return ['invalid', 'Une carte distribution doit avoir au moins une promo.'];
    }
    return 'valid';
}

async function translationValidator(context: ValidationContext<PokemonCardDTO | EditedPokemonCard, any>): Promise<ValidationResult> {
    if (!isTranslationValid(context.data, context.path)) {
        return 'valid';
    }

    const result = await defaultValidator(createValidationContext(context.data, {
        path: context.path,
        mergeData: context.mergeData.filter(d => isTranslationValid(d, context.path)),
        optional: context.required,
        reviewed: context.reviewed,
        rule: context.rule
    }));

    if (getValidationStatus(result) !== 'invalid' && (pathEndsWith(context.path, 'available') || pathEndsWith(context.path, 'localization'))) {
        return 'valid';
    } else if (!context.value && context.mergeValues.some(a => a)) {
        return {
            status: 'invalid',
            reviewable: true,
            message: 'Ce champ existe en base.',
            legendKeys: ["breaking-change"]
        };
    }
    return result;
}

async function getExpectedLocalizations(context: ValidationContext<PokemonCardDTO | EditedPokemonCard, Translations<PokemonCardTranslationDTO>>) {
    const setLocalizations: LocalizationCode[] = [];

    for (const l of localizationsWithSet) {
        if (l in context.data && (context.data as EditedPokemonCard)[l]?.set?.translations) {
            Object.values(((context.data as EditedPokemonCard)[l]?.set as PokemonSetDTO).translations).forEach(t => {
                if (t?.localization) {
                    setLocalizations.push(t.localization);
                }
            });
        }
    }
    if ('setIds' in context.data && context.data.setIds) {
        (await getSetLocalizationsFromIds(context.data.setIds)).forEach(l => setLocalizations.push(l));
    }
    return chain(context.mergeData)
        .flatMap(c => Object.keys(c.translations))
        .uniq()
        .union(setLocalizations)
        .value() as LocalizationCode[];
}
async function missingTranslationValidator(context: ValidationContext<PokemonCardDTO | EditedPokemonCard, Translations<PokemonCardTranslationDTO>>): Promise<ValidationResult> {
    if (context.reviewed) {
        return 'valid';
    }

    const availableLocalizations = Object.keys(context.data.translations);
    const expectedLocalizations = (await getExpectedLocalizations(context)).filter(l => !availableLocalizations.includes(l));

    if (expectedLocalizations.length > 0) {
        return {
            status: 'invalid',
            message: 'Traductions manquantes',
            reviewable: true,
            legendKeys: ['missing-translation']
        }
    }
    return 'valid';
}

function useTranslationRules(sourceTranslations?: MaybeRefOrGetter<SourcedPokemonCardTranslationDTO[]>): ValidationRules<PokemonCardDTO | EditedPokemonCard, Translations<PokemonCardTranslationDTO>> {
    const nameValidator = useNameValidator();
    const sourceTranslationValidator = useSourceTranslationValidator(sourceTranslations ?? []);

    return {
        required: 'optional',
        validators: [missingTranslationValidator],
        each: {
            keys: localizationCodes,
            number: {validators: [translationValidator, sourceTranslationValidator], required: 'optional' },
            rarity: {validators: [translationValidator, sourceTranslationValidator], required: 'optional' },
            name: {validators: [translationValidator, nameValidator, sourceTranslationValidator], required: 'optional' },
            labelName: {validators: [translationValidator, nameValidator, sourceTranslationValidator], required: 'optional' },
            originalName: {validators: [translationValidator, sourceTranslationValidator],  required: 'optional' },
            trainer: {validators: [translationValidator, sourceTranslationValidator], required: 'optional' },
        }
    };
}

const charsetRules: ValidationRules<PokemonCardDTO | EditedPokemonCard, EditedPokemonCardSpecialTranslation> = {
    brackets: {
        validators: [arrayValidator<PokemonCardDTO | EditedPokemonCard, BracketDTO>(true)],
        each: { mapped: bracketValidationRules, required: 'optional' as Requirement, }
    }
};
function useCommonRules(sourceTranslations?: MaybeRefOrGetter<SourcedPokemonCardTranslationDTO[]>): ValidationRules<PokemonCardDTO | EditedPokemonCard> {
    return {
        type: {validators: [defaultValidator]},
        type2: {validators: [defaultValidator], required: 'optional'},
        artist: {validators: [defaultValidator], required: 'optional'},
        level: {
            validators: [defaultValidator],
            required: 'optional'
        },
        tags: {
            validators: [arrayValidator<PokemonCardDTO | EditedPokemonCard, CardTagDTO>(true), tagsValidator],
            each: {
                validators: [defaultValidator, tagValidator],
                required: 'optional',
            }
        },
        translations: useTranslationRules(toValue(sourceTranslations) ?? []),
        parentId: {validators: [parentIdValidator],},
        promos: {
            validators: [arrayValidator<PokemonCardDTO | EditedPokemonCard, PromoCardDTO>(true), promosValidator],
            each: { mapped: promoCardValidationRules },
        }
    };
}
export function createPokemonCardRules(sourceTranslations?: MaybeRefOrGetter<SourcedPokemonCardTranslationDTO[]>): ValidationRules<PokemonCardDTO> {
    return {
        ...useCommonRules(sourceTranslations),
        featureIds: { validators: [arrayValidator<PokemonCardDTO, string>(true)], each: { required: 'optional' } },
        setIds: {
            validators: [arrayValidator<PokemonCardDTO, string>(true)],
            each: {
                validators: [defaultValidator],
                required: 'optional',
            }
        },
        ...charsetRules
    } as ValidationRules<PokemonCardDTO>;
}
export function usePokemonCardRules(sourceTranslations?: MaybeRefOrGetter<SourcedPokemonCardTranslationDTO[]>) {
    return computed<ValidationRules<PokemonCardDTO>>(() => createPokemonCardRules(sourceTranslations));
}

export function createEditedPokemonCardRules(sourceTranslations?: MaybeRefOrGetter<SourcedPokemonCardTranslationDTO[]>): ValidationRules<EditedPokemonCard> {
    const cr: ValidationRules<EditedPokemonCard, EditedPokemonCardSpecialTranslation> = {
        set: { validators: [defaultValidator] },
        ...charsetRules
    };
    const crl = invoke(() => {
        const result: ValidationRules<EditedPokemonCard> = {};

        for (const l of localizationsWithSet) {
            result[l] = cr;
        }
        return result;
    });

    return {
        ...useCommonRules(sourceTranslations),
        ...crl,
        features: { validators: [arrayValidator<EditedPokemonCard, FeatureDTO>(true)], each: { required: 'optional' } }
    } as ValidationRules<EditedPokemonCard>;
}
export function useEditedPokemonCardRules(sourceTranslations?: MaybeRefOrGetter<SourcedPokemonCardTranslationDTO[]>) {
    return computed<ValidationRules<EditedPokemonCard>>((): ValidationRules<EditedPokemonCard> => createEditedPokemonCardRules(sourceTranslations));
}

export function useValidateTranslation(card: MaybeRefOrGetter<PokemonCardDTO | EditedPokemonCard>, sourceTranslations?: MaybeRefOrGetter<SourcedPokemonCardTranslationDTO[]>) {
    const rules = useTranslationRules(sourceTranslations).each;

     return (async (v: any, p: Path, optional?: boolean, reviewed?: boolean): Promise<ValidationResult> => {
        const copy = cloneDeep(toValue(card));
        const target = toPath(p)[2];

        set(copy, p, v);
        return getStrongestValidationStatus(await validatePath<PokemonCardDTO | EditedPokemonCard, PokemonCardTranslationDTO>((rules as any)[target], p, copy, [], optional, reviewed));
    });
}
