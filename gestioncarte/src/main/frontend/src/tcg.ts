import {useRoute} from "vue-router";
import {computed} from "vue";
import {isString} from "lodash";


const tcgs = {
    "pok": "pokemon",
    "mag": "magic",
    "ygh": "yugioh",
    "onp": "onepiece",
    "lor": "lorcana",
} as const;

export type TradingCardGameKey = keyof typeof tcgs;
export type TradingCardGame = typeof tcgs[keyof typeof tcgs];

export const useTcg = () => {
    const route = useRoute();

    return computed<TradingCardGame | undefined>(() => {
        const tcg = route.params?.tcg;

        if (isString(tcg) && Object.values(tcgs).includes(tcg as any)) {
            return tcg as TradingCardGame;
        }
        return undefined;
    });
}

export const getTcg = (s: string): TradingCardGame | undefined => {
    if (Object.values(tcgs).includes(s as any)) {
        return s as TradingCardGame;
    } else if (Object.keys(tcgs).includes(s as any)) {
        return tcgs[s as TradingCardGameKey] as TradingCardGame;
    }
    return undefined;
}
