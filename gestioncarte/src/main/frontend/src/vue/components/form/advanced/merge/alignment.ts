import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import {isInComponentSetup} from "@/vue/composables/ComposablesHelper";
import {useRaise} from "@/alert";
import {onUpdated, Ref, ref, watch} from "vue";
import {chain, groupBy} from "lodash";
import log from "loglevel";
import {MaybeRefOrGetter, toValue} from "@vueuse/core";
import {shallowRef} from "@vue/reactivity";
import {useAdvancedFormSide} from "@components/form/advanced/merge/logic";
import {triggerRef} from "vue-demi";

type AlignedElement = {
    key: string,
    element: HTMLElement,
    side: number,
    offset: number
};
type AlignmentContext = {
    elements: Ref<AlignedElement[]>
    disabled: Ref<boolean>
}

const [_useProvideAlignmentContext, _useAlignmentContext, _alignmentContextKey] = ModelComposables.createInjectionState((): AlignmentContext => {
    const elements = ref<AlignedElement[]>([]);
    const disabled = ref(false);

    return { elements, disabled };
}, "aligned elements");

export const useProvideAlignmentContext = _useProvideAlignmentContext;
export const alignmentContextKey = _alignmentContextKey as symbol;

const useUpdateAlignedElement = () => {
    if (!isInComponentSetup()) {
        return;
    }

    const context = _useAlignmentContext();
    const raise = useRaise();

    onUpdated(() => {
        if (!context) {
            return;
        }

        const groups = Object.values(groupBy(context.elements.value, c => c.key));
        const sides = chain(context.elements.value)
            .map(c => c.side)
            .uniq()
            .value();
        let hasChanged = false;

        if (sides.length > 4) {
            log.log(`there are too many sides (${sides.length}) and alignment has been turned off`);
            raise.info("En raison d'un trop grand nombre de colonnes, l'alignement des éléments du formulaire a été désactivé");
            context.disabled.value = true;
        }
        if (context.disabled.value || sides.length <= 1) {
            for (const group of groups) {
                for (const e of group) {
                    if (e.offset !== 0) {
                        setOffset(e, 0);
                        hasChanged = true;
                    }
                }
            }
        } else {
            for (const group of groups) {
                const offsets = group.map(c => getOffset(c.element) - c.offset);
                const minOffset = Math.min(...offsets);
                const maxOffset = Math.max(...offsets);
                const key = group[0].key;

                if (maxOffset - minOffset >= 10000) {
                    log.error(`aligned element offset for key: ${key} is too big (${maxOffset - minOffset}) and has ben turned off`);
                    raise.warn("Une erreur est survenue lors de l'alignement des éléments du formulaire");
                    context.disabled.value = true;
                    return;
                } else if (maxOffset - minOffset >= 1000) {
                    log.warn(`aligned element offset for key: ${key} is too big (${maxOffset - minOffset}), this may cause performance issues`);
                }


                for (const [i, e] of group.entries()) {
                    const offset = maxOffset - offsets[i];
                    const oldOffset = e.offset;

                    if (offset !== oldOffset) {
                        setOffset(e, offset);
                        hasChanged = true;
                    }
                }
            }
        }
        if (hasChanged) {
            context.elements.value = groups.flatMap(g => g);
        }
    });
}

export const useAlignedElement = (key: MaybeRefOrGetter<string>) => {
    const context = _useAlignmentContext();
    const side = useAdvancedFormSide();

    const element = shallowRef<HTMLElement>();
    const index = ref(-1);

    watch([() => toValue(element), () => toValue(key)], ([e, k]) => {
        if (!context || !e || !k) {
            return;
        }

        if (index.value === -1) {
            index.value = context.elements.value.length;
            context.elements.value.push({ key: k, element: e, side: side.value, offset: 0 });
        } else {
            const a = context.elements.value[index.value];

            if (a.key !== k || a.element !== e || a.side !== side.value) {
                a.key = k;
                a.element = e;
                a.side = side.value;
            }
        }
        triggerRef(context.elements);
    });
    // TODO: remove element from context on unmount
    useUpdateAlignedElement();
    return {
        ref: element,
        'data-align-key': toValue(key),
        class: "advanced-form-aligned-element",
    }
}

function getAlignedElement(element: HTMLElement) {
    let el: HTMLElement | null = element;

    while (el && !el.classList.contains("advanced-form-aligned-element") && !el.classList.contains("advanced-form")) {
        el = el.parentElement;
    }
    return el;
}

function getOffset(el: HTMLElement) {
    const element = getAlignedElement(el);
    const parent = element?.parentElement ? getAlignedElement(element.parentElement) : undefined;
    const offset = element?.getBoundingClientRect().top ?? 0;
    const parentOffset = parent?.getBoundingClientRect().top ?? 0;

    return offset - parentOffset;
}

function setOffset(e: AlignedElement, offset: number) {
    e.offset = offset;
    e.element.style.marginTop = offset === 0  ? '' : `${offset}px`;
    log.debug(`set offset of '${e.key}' to ${offset}`);
}
