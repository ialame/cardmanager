import {
    createGlobalState,
    MaybeRef,
    MaybeRefOrGetter,
    Position,
    toValue,
    useDraggable,
    useEventListener
} from "@vueuse/core";
import {computed, onMounted, onUnmounted, Ref, ref} from "vue";
import {DOMComposables} from "@/vue/composables/DOMComposables";

export namespace DragAndDropComposables {

    type DropElement = MaybeRef<HTMLElement | SVGElement | null | undefined>;

    type DropZone<T> = {
        target: DropElement,
        onDrop: (t: T) => void
    }

    class DropZoneSet<T> {
        zones: Set<DropZone<T>>;
        isDragging: boolean;
        constructor() {
            this.zones = new Set();
            this.isDragging = false;
        }
    }
    const dropZones = new Map<string, Ref<DropZoneSet<any>>>();

    export function useDrag<T>(type: string, target: DropElement, data?: MaybeRefOrGetter<T>) {
        const isMouseInRect = DOMComposables.useIsMouseInRect();
        const startPos = ref<Position>({x: 0, y: 0});
        const dropZoneSet = useDropZoneSet(type);
        const {x, y, isDragging} = useDraggable(target, {
            onStart: (_p: Position, event: PointerEvent) => {
                const t = event.target as HTMLElement;

                if (['INPUT', 'TEXTAREA', 'A', 'BUTTON', 'SELECT', 'OPTION'].includes(t.tagName) || t.classList.contains('not-draggable')) {
                    return false;
                }

                const rect = DOMComposables.getRect(target);

                startPos.value = rect ? {x: rect.left || 0, y: rect.top || 0} : {x: 0, y: 0};
                dropZoneSet.value.isDragging = true;
            },
            onEnd: () => {
                dropZoneSet.value.zones.forEach(({target: t, onDrop}) => {
                    if (isMouseInRect(t)) {
                        onDrop(toValue(data));
                    }
                });
                dropZoneSet.value.isDragging = false;
            },
        });
        const style = computed(() => `touch-action:none;left:${x.value - startPos.value.x}px;top:${y.value - startPos.value.y}px;`);
        return {x, y, isDragging, style};
    }

    export function useDrop<T>(type: string, target: DropElement, onDrop: (t: T) => void) {
        const bag = {target, onDrop};
        const dropZoneSet = useDropZoneSet(type);

        onMounted(() => dropZoneSet.value.zones.add(bag));
        onUnmounted(() => dropZoneSet.value.zones.delete(bag));
        return computed(() => dropZoneSet.value.isDragging);
    }

    export function useDropZoneSet<T>(type: string) {
        if (!dropZones.has(type)) {
            dropZones.set(type, ref(new DropZoneSet<T>()));
        }
        return dropZones.get(type)!;
    }

    export const useDragging = createGlobalState(() => {
        const dragging = ref(false);
        const dragTimer = ref<number | null>(null);

        useEventListener(document, 'dragover', () => {
            dragging.value = true;
            if (dragTimer.value) {
                window.clearTimeout(dragTimer.value);
            }
        });
        useEventListener(document, 'dragleave', () => {
            dragTimer.value = window.setTimeout(() => {
                dragging.value = false;
            }, 100);
        });
        return dragging;
    });

}
