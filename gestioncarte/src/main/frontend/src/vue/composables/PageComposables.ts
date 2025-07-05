import {createGlobalState, MaybeRefOrGetter, toValue, useTitle} from "@vueuse/core";
import {computed, ref, watchEffect} from "vue";
import {useRoute} from "vue-router";
import {isNil} from "lodash";
import {useTcg} from "@/tcg";

const prefix = "Professional Card Retriever";

const _usePageTitle = createGlobalState(() => {
    const rawTitle = useTitle(prefix);

    return computed({
        get: () => {
            if (!rawTitle.value) {
                return prefix;
            } else if (rawTitle.value.startsWith(prefix + " - ")) {
                return rawTitle.value.split(prefix + " - ")[1] || '';
            } else if (rawTitle.value.startsWith(prefix)) {
                return rawTitle.value.split(prefix)[1];
            }
            return rawTitle.value;
        },
        set: (value?: string) => rawTitle.value = prefix + (value ? " - " + value : "")
    });
});

export const getPageTitle = _usePageTitle;
export const usePageTitle = (t?: MaybeRefOrGetter<string>) => {
    const tcg = useTcg();
    const title = _usePageTitle();

    watchEffect(() => {
        let prefix = '';

        switch (toValue(tcg)) {
            case "pokemon":
                prefix = "Pokémon - ";
                break;
            case "magic":
                prefix = "Magic - ";
                break;
            case "yugioh":
                prefix = "Yu-Gi-Oh! - ";
                break;
            case "onepiece":
                prefix = "One Piece - ";
                break;
            case "lorcana":
                prefix = "Lorcana - ";
                break;
        }
        const v = toValue(t);

        return title.value = v ? (prefix + v) : '';
    });
    return title;
}

export const useSideBarTabs = createGlobalState(() => {
    const route = useRoute();
    const tcg = ref<string>("");

    watchEffect(() => {
        const path = route.path;

        if (path.includes("pokemon")) {
            tcg.value = "pokemon";
        } else if (path.includes("magic")) {
            tcg.value = "magic";
        } else if (path.includes("yugioh")) {
            tcg.value = "yugioh";
        } else if (path.includes("onepiece")) {
            tcg.value = "onepiece";
        }else if (path.includes("lorcana")) {
            tcg.value = "lorcana";
        } else {
            tcg.value = "";
        }
    });
    return tcg;
});

function getTcgIndex(tcg: string) {
    switch (tcg) {
        case "pokemon":
            return 1;
        case "magic":
            return 2;
        case "yugioh":
            return 3;
        case "onepiece":
            return 4;
        case "lorcana":
            return 5;
        default:
            return 0;
    }
}
export function compareTcgNames(a: string, b: string) {
    return getTcgIndex(a) - getTcgIndex(b);
}

export const useCommaSeparatedParam = (param: string) => {
    const route = useRoute();

    return computed<string[]>((() => {
        const p = route.params[param]

        if (Array.isArray(p)) {
            return p.map(v => v.split(','))
                .flat();
        } else if (!isNil(p)) {
            return p.split(',');
        }
        return [];
    }));
}
