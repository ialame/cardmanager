import {computed} from "vue";
import {MaybeRefOrGetter, toValue} from "@vueuse/core";
import {usePageTitle} from "@/vue/composables/PageComposables";
import {LorcanaCardDTO} from "@/types";

export const useLorcanaTitle = (title: MaybeRefOrGetter<string>) => usePageTitle(computed(() => `Lorcana - ${toValue(title)}`));

export const getLorcanaCardName = (card: LorcanaCardDTO) => card.translations.us?.title ? `${card.translations.us.name} - ${card.translations.us.title}` : card.translations.us?.name ?? "";
