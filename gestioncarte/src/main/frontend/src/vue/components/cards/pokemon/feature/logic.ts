import {LocalizationCode} from "@/localization";
import {computedAsync, MaybeRefOrGetter, toValue} from "@vueuse/core";
import {pokemonCardTranslatorService} from "@components/cards/pokemon/translators";
import {RestComposables} from "@/vue/composables/RestComposables";
import createSharedRestComposable = RestComposables.createSharedRestComposable;


export const useSources = (localization: MaybeRefOrGetter<LocalizationCode | undefined>) => {
    return computedAsync(async () => {
        const l = toValue(localization);

        return (l !== undefined ? await pokemonCardTranslatorService.find(s => s.patternLocalizations.includes(l)) : pokemonCardTranslatorService.all.value).map(s => s.code)
    });
}

export const useSource = (source: MaybeRefOrGetter<string | undefined>) => {
    return computedAsync(async () => {
        const s = toValue(source);

        return s !== undefined ? (await pokemonCardTranslatorService.find(t => t.code === s))[0] : undefined;
    });
}

export const useWikiNames = createSharedRestComposable<string[]>("/api/cards/pokemon/features/wikis/names", []);
