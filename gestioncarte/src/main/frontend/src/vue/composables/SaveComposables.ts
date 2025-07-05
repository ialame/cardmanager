import {ref} from "vue";
import {createGlobalState, MaybeRefOrGetter, onKeyStroke, toValue} from "@vueuse/core";


export namespace SaveComposables {

    type Save = () => void;
    type AsyncSave = () => Promise<void>;

    export const useGlobalSaveLock = createGlobalState(() => ref(false));

    export function useLockedSave(s: Save) {
        const lock = useGlobalSaveLock();
        const save = () => {
            if (lock.value) {
                return;
            }
            lock.value = true;
            s();
            lock.value = false;
        };

        return {lock, save};
    }

    export function useLockedSaveAsync(s: AsyncSave) {
        const lock = useGlobalSaveLock();
        const save = async () => {
            if (lock.value) {
                return;
            }
            lock.value = true;
            await s();
            lock.value = false;
        };

        return {lock, save};
    }

    export function useSaveShortcut(save: Save | AsyncSave, active?: MaybeRefOrGetter<boolean>) {
        onKeyStroke(e => e.key === "s" && (e.ctrlKey || e.metaKey), e => {
            const a = toValue(active);

            if (a === undefined || a) {
                e.preventDefault();
                save();
            }
        });
    }

    export function useSave(s: Save, shortcutActive?: MaybeRefOrGetter<boolean>) {
        const {save} = useLockedSave(s);
        useSaveShortcut(save, shortcutActive);

        return save;
    }

    export function useSaveAsync(s: AsyncSave, shortcutActive?: MaybeRefOrGetter<boolean>) {
        const {save} = useLockedSaveAsync(s);
        useSaveShortcut(save, shortcutActive);

        return save;
    }

}
