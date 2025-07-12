import {createApp} from "vue";
import {fireEvent} from "@testing-library/dom";
import {watchOnce} from "@vueuse/core";
import {WatchSource} from "vue-demi";

export namespace TestHelper {

    export function withSetup<T>(composable: () => T, e?: HTMLElement) {
        let result: T;
        const app = createApp({
            setup() {
                result = composable();

                return () => {}
            }
        });

        app.mount(e ? e : document.createElement('div'))
        // @ts-ignore
        return {result, app};
    }

    export function moveMouse(x: number, y: number) {
        fireEvent.pointerMove(window, {pageX: x, pageY: y});
        fireEvent.mouseMove(window, {pageX: x, pageY: y, clientX: x, clientY: y});
    }

    export const createRect = (x: number, y: number, w: number, h: number): DOMRect => ({
        x: x,
        y: y,
        top: y,
        left: x,
        bottom: y + h,
        right: x + w,
        width: w,
        height: h,
        toJSON: () => JSON.stringify({x, y, w, h})
    });

    export const nextTrigger = <T>(ref: WatchSource<T>) => new Promise<T>(resolve => watchOnce(ref, t => resolve(t)));
}
