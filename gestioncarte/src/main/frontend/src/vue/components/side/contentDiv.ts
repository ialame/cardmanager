import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import {MaybeRefOrGetter, toRef} from "@vueuse/core";

export const [provideContentDiv, useContentDiv] = ModelComposables.createInjectionState((e: MaybeRefOrGetter<HTMLDivElement | undefined>) => toRef(e));
