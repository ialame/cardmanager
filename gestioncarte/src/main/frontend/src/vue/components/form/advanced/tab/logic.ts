import {indexOf} from "lodash";
import {ModelComposables} from "@/vue/composables/model/ModelComposables";
import {MaybeRefOrGetter, toRef, toValue} from "@vueuse/core";
import {computed, ref, watch, watchEffect} from "vue";
import {addDevtoolTags} from "@/vue/devtool";
import {useAdvancedFormSide} from "@components/form/advanced/merge";

export type TabControl = Record<string, boolean>;

const [useProvideAdvancedFormTabControl, useAdvancedFormTabControl, _advancedFormTabKeyControl] = ModelComposables.createInjectionState((tabControlRef?: MaybeRefOrGetter<TabControl>) => {
    const openedTab = ref("");
    const tabs = ref<string[]>([]);
    const tabControl = toRef(tabControlRef);
    const activeTabs = computed(() => tabs.value.filter(t => tabControl.value?.[t] !== false) ?? []);

    const setTab = (tab: string) => {
        openedTab.value = tab;
    }
    const goToNextTab = () => {
        let index = indexOf(activeTabs.value, openedTab.value) + 1;

        if (index >= activeTabs.value.length) {
            index = 0;
        }
        const next = activeTabs.value[index];

        if (next) {
            setTab(next);
        }
    };
    const goToPreviousTab = () => {
        let index = indexOf(activeTabs.value, openedTab.value) - 1;

        if (index < 0) {
            index = activeTabs.value.length - 1;
        }
        const next = activeTabs.value[index];

        if (next) {
            setTab(next);
        }
    };

    _useProvideAdvancedFormTab('');
    watchEffect(() => {
        if (activeTabs.value.length > 0 && !activeTabs.value.includes(openedTab.value)) {
            setTab(activeTabs.value[0]);
        }
    })

    return { openedTab, tabs, activeTabs, setTab, goToNextTab, goToPreviousTab };
}, "advanced form tab control");

export const advancedFormTabKeyControl = _advancedFormTabKeyControl as symbol;
export {useProvideAdvancedFormTabControl, useAdvancedFormTabControl};

const [_useProvideAdvancedFormTab, _useAdvancedFormTab, _advancedFormTabKey] = ModelComposables.createInjectionState((tab: MaybeRefOrGetter<string>) => toRef(tab), "advanced form tab");

export const advancedFormTabKey = _advancedFormTabKey as symbol;
export const useAdvancedFormTab = () => _useAdvancedFormTab() ?? (() => ref(""))();

export const useProvideAdvancedFormTab = (t: MaybeRefOrGetter<string>) => {
    const tabControl = useAdvancedFormTabControl();
    const side = useAdvancedFormSide();

    _useProvideAdvancedFormTab(t);

    const opened = computed(() => !tabControl || tabControl.openedTab.value === toValue(t));

    watch(() => toValue(t), (n, o) => {
        if (side.value !== -1 || n === o || !tabControl) {
            return;
        }

        let v = tabControl.tabs.value.map(t => t === o ? n : t);

        if (!v.includes(n)) {
            v = [ ...v, n ];
        }
        tabControl.tabs.value = v;
    }, { immediate: true });
    setDevtoolTab(t);
    return { opened };
}

export function useIsInOpenedTab() {
    const tabControl = useAdvancedFormTabControl();
    const tab = useAdvancedFormTab();

    return computed(() => !tab.value || !tabControl || tabControl?.openedTab.value === tab.value);
}

const setDevtoolTab = (tab: MaybeRefOrGetter<string>) => {
    const t = toValue(tab);

    addDevtoolTags({
        label: t,
        textColor: 0xffffff,
        backgroundColor: 0x22b24c,
        tooltip: `tab: ${t}`
    });
}
