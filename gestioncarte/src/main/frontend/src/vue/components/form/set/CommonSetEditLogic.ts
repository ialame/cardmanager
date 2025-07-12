import {ref} from "vue";
import {LocalizationCode} from "@/localization";
import {MaybeRefOrGetter, toValue} from "@vueuse/core";

export namespace CommonSetEditLogic {

    export const maxDate = (() => {
        const value = new Date();

        return `${value.getFullYear() + 5}-01-01`;
    })();

    export const useLocalizationCollapse = (localizations: MaybeRefOrGetter<LocalizationCode[]>) => {
        const requiredLocalizations = ref<LocalizationCode[]>(toValue(localizations));
        const toggleCollapse = (l: LocalizationCode, o: boolean) => {
            if (o) {
                requiredLocalizations.value.push(l);
            } else {
                requiredLocalizations.value = requiredLocalizations.value.filter(l2 => l2 !== l);
            }
        };

        return { requiredLocalizations, toggleCollapse };
    }

}
