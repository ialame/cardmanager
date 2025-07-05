import {computedAsync, createSharedComposable} from "@vueuse/core";
import rest from "@/rest";
import {LocalizationCode} from "@/localization";

export const useYuGiOhLocalizations = createSharedComposable(() => computedAsync<LocalizationCode[]>(() => rest.get("/api/localizations/groups/yugioh")));
