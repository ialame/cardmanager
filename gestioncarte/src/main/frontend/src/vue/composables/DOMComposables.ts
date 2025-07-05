import {createSharedComposable, MaybeRefOrGetter, toValue, useEventListener, useMouse} from "@vueuse/core";
import {computed, ref, Ref} from "vue";
import {isNil} from "lodash";

export namespace DOMComposables {

    function isOverflown(el: HTMLElement): boolean { // eslint-disable-line no-inner-declarations
        return el !== undefined && (el.scrollHeight > el.clientHeight || el.scrollWidth > el.clientWidth);
    }

    export function useOverflown(element: MaybeRefOrGetter<HTMLElement>): Ref<boolean> {
        const overflown = ref(false);
        useEventListener("input", () => overflown.value = isOverflown(toValue(element)));

        return overflown;
    }

    /**
     * @deprecated
     */
    export function useSetUrl() {
        return (title: string, url: string) => window.history.pushState(url, title, url);
    }

    export const useSharedMouse = createSharedComposable(() => useMouse({type: 'client'}));

    type ElementOrRect = MaybeRefOrGetter<Element | DOMRect | null | undefined>;

    export const getRect = (element: ElementOrRect): DOMRect | undefined => {
        const e = toValue(element);

        if (e === undefined || e === null) {
            return undefined;
        }
        return e instanceof Element ? e.getBoundingClientRect() : e;
    }

    export function useIsMouseInRect(): ((r: ElementOrRect) => boolean);
    export function useIsMouseInRect(rectOrElement: ElementOrRect): Ref<boolean>;
    export function useIsMouseInRect(rectOrElement?: ElementOrRect): Ref<boolean> | ((r: ElementOrRect) => boolean) {
        const { x, y } = useSharedMouse();
        const isInRect = (r: ElementOrRect): boolean => {
            const rect = getRect(r);

            return !isNil(rect) && x.value >= rect.left && x.value <= rect.right && y.value >= rect.top && y.value <= rect.bottom;
        }

        return rectOrElement ? computed(() => isInRect(rectOrElement)) : isInRect;
    }

}
